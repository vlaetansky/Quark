package vazkii.quark.content.management.module;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.util.function.BooleanSupplier;

import org.apache.commons.lang3.tuple.Pair;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.recipebook.GhostRecipe;
import net.minecraft.client.gui.recipebook.GhostRecipe.GhostIngredient;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.recipebook.RecipeBookPage;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseClickedEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;

@LoadModule(category =  ModuleCategory.CLIENT, hasSubscriptions = true, subscribeOn = Dist.CLIENT)
public class MicrocraftingHelperModule extends QuarkModule {

	private static Screen currentScreen;
	private static Stack<StackedRecipe> recipes = new Stack<>(); 
	private static int compoundCount = 1;

	@SubscribeEvent
	public void onClick(MouseClickedEvent.Pre event) {
		Minecraft mc = Minecraft.getInstance();
		Screen screen = mc.currentScreen;

		if(screen instanceof CraftingScreen && event.getButton() == 1) { // TODO more inclusive checking
			CraftingScreen cscreen = (CraftingScreen) screen;
			RecipeBookGui recipeBook = cscreen.getRecipeGui();

			Pair<GhostRecipe, GhostIngredient> pair = getHoveredGhost(cscreen, recipeBook);
			if(pair != null) {
				GhostRecipe ghost = pair.getLeft();
				GhostIngredient ghostIngr = pair.getRight();
				Ingredient ingr = ObfuscationReflectionHelper.getPrivateValue(GhostIngredient.class, ghostIngr, "ingredient"); // TODO AT

				IRecipe<?> recipeToSet = getRecipeToSet(recipeBook, ingr, true);
				if(recipeToSet == null)
					recipeToSet = getRecipeToSet(recipeBook, ingr, false);

				if(recipeToSet != null) {
					int ourCount = 0;

					ItemStack testStack = recipeToSet.getRecipeOutput();
					for(int j = 1; j < ghost.size(); j++) { // start at 1 to skip output
						GhostIngredient testGhostIngr = ghost.get(j);
						Ingredient testIngr = ObfuscationReflectionHelper.getPrivateValue(GhostIngredient.class, testGhostIngr, "ingredient"); // TODO AT

						if(testIngr.test(testStack)) // TODO NBT sensitivity?
							ourCount++;
					}

					if(ourCount > 0) {
						StackedRecipe stackedRecipe = new StackedRecipe(ghost.getRecipe(), compoundCount, getClearCondition(ingr));
						recipes.add(stackedRecipe);

						compoundCount *= ourCount;
					}

					ghost.clear();
					mc.playerController.sendPlaceRecipePacket(mc.player.openContainer.windowId, recipeToSet, true);
				}

				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onDrawGui(DrawScreenEvent.Post event) {
		if(!recipes.isEmpty()) {
			Minecraft mc = Minecraft.getInstance();

			Screen screen = mc.currentScreen;
			if(screen instanceof CraftingScreen) { // TODO more inclusive checking
				CraftingScreen cscreen = (CraftingScreen) screen;
				MatrixStack mstack = event.getMatrixStack();
				ItemRenderer render = mc.getItemRenderer();
				int left = cscreen.getGuiLeft() + 95;
				int top = cscreen.getGuiTop() + 6;

				mc.textureManager.bindTexture(MiscUtil.GENERAL_ICONS);
				Screen.blit(mstack, left, top, 0, 0, 108, 80, 20, 256, 256);

				int start = Math.max(0, recipes.size() - 3);
				for(int i = start; i < recipes.size(); i++) {
					int index = i - start;
					StackedRecipe recipe = recipes.get(i);
					int x = left +index * 24 + 2;
					int y = top + 2;

					ItemStack copy = recipe.recipe.getRecipeOutput().copy();
					copy.setCount(recipe.count);
					render.renderItemIntoGUI(copy, x, y);
					render.renderItemOverlays(mc.fontRenderer, copy, x, y);

					if(index > 0)
						mc.fontRenderer.drawString(mstack, "<", x - 6, y + 4, 0x3f3f3f);
				}
				
				Pair<GhostRecipe, GhostIngredient> pair = getHoveredGhost(cscreen, cscreen.getRecipeGui());
				if(pair != null) {
					GhostIngredient ingr = pair.getRight();
					if(ingr != null) {
						List<ITextProperties> tooltip = Arrays.asList(new TranslationTextComponent("Right Click to Craft"));
						cscreen.renderWrappedToolTip(mstack, tooltip, event.getMouseX(), event.getMouseY() - 15, mc.fontRenderer);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		Minecraft mc = Minecraft.getInstance();
		Screen prevScreen = currentScreen;
		currentScreen = mc.currentScreen;

		// TODO changing recipe does not clear the stack as it should
		if(prevScreen != currentScreen)
			recipes.clear();

		if(!recipes.isEmpty()) {
			StackedRecipe top = recipes.peek();
			if(top.clearCondition.getAsBoolean()) {
				mc.playerController.sendPlaceRecipePacket(mc.player.openContainer.windowId, top.recipe, true);
				compoundCount = top.count;
				recipes.pop();
			}
		} 
		else compoundCount = 1;
	}

	private IRecipe<?> getRecipeToSet(RecipeBookGui recipeBook, Ingredient ingr, boolean craftableOnly) {
		TextFieldWidget text = ObfuscationReflectionHelper.getPrivateValue(RecipeBookGui.class, recipeBook, "searchBar"); // TODO AT

		for(ItemStack stack : ingr.getMatchingStacks()) {
			text.setText(stack.getDisplayName().copyRaw().getString().toLowerCase(Locale.ROOT));

			try {
				Method update = ObfuscationReflectionHelper.findMethod(RecipeBookGui.class, "updateSearch", new Class[0]); // TODO AT
				update.invoke(recipeBook);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}

			RecipeBookPage page = ObfuscationReflectionHelper.getPrivateValue(RecipeBookGui.class, recipeBook, "recipeBookPage"); // TODO AT
			if(page != null) {
				List<RecipeList> recipeLists = ObfuscationReflectionHelper.getPrivateValue(RecipeBookPage.class, page, "recipeLists"); // TODO AT
				recipeLists = new ArrayList<>(recipeLists); // ensure we're not messing with the original
				
				if(recipeLists != null && recipeLists.size() > 0) {
					recipeLists.removeIf(rl -> {
						List<IRecipe<?>> list = rl.getDisplayRecipes(craftableOnly);
						return list == null || list.isEmpty();
					});
					
					if(recipeLists.isEmpty())
						return null;
					
					Collections.sort(recipeLists, (rl1, rl2) -> {
						if(rl1 == rl2)
							return 0;
						
						IRecipe<?> r1 = rl1.getDisplayRecipes(craftableOnly).get(0);
						IRecipe<?> r2 = rl2.getDisplayRecipes(craftableOnly).get(0);
						return compareRecipes(r1, r2);
					});
					
					for(RecipeList list : recipeLists) {
						List<IRecipe<?>> recipeList = list.getDisplayRecipes(craftableOnly);
						Collections.sort(recipeList, this::compareRecipes);
						
						for(int i = 0; i < recipeList.size(); i++)
							if(ingr.test(recipeList.get(i).getRecipeOutput()))
								return recipeList.get(i);
					}
				}
			}
		}

		return null;
	}
	
	private int compareRecipes(IRecipe<?> r1, IRecipe<?> r2) {
		if(r1 == r2)
			return 0;
		
		String id1 = r1.getId().toString();
		String id2 = r2.getId().toString();

		boolean id1Mc = id1.startsWith("minecraft");
		boolean id2Mc = id2.startsWith("minecraft");
		
		if(id1Mc != id2Mc)
			return id1Mc ? -1 : 1;
		
		return id1.compareTo(id2);
	}

	private BooleanSupplier getClearCondition(Ingredient ingr) {
		Minecraft mc = Minecraft.getInstance();
		return () -> {
			int missing = compoundCount;
			for(ItemStack invStack : mc.player.inventory.mainInventory) {
				if(ingr.test(invStack)) {
					missing -= invStack.getCount();

					if(missing <= 0)
						return true;
				}
			}

			return false;
		};
	}

	private Pair<GhostRecipe, GhostIngredient> getHoveredGhost(ContainerScreen<?> cscreen, RecipeBookGui recipeBook) {
		Slot slot = cscreen.getSlotUnderMouse();

		if(recipeBook != null && slot != null) {
			GhostRecipe ghost = ObfuscationReflectionHelper.getPrivateValue(RecipeBookGui.class, recipeBook, "ghostRecipe"); // TODO AT
			if(ghost != null && ghost.getRecipe() != null) {
				for(int i = 1; i < ghost.size(); i++) { // start at 1 to skip output
					GhostIngredient ghostIngr = ghost.get(i);

					if(ghostIngr.getX() == slot.xPos && ghostIngr.getY() == slot.yPos)
						return Pair.of(ghost, ghostIngr);
				}
			}
		}

		return null;
	}

	private static class StackedRecipe {

		public final IRecipe<?> recipe;
		public final int count;
		public final BooleanSupplier clearCondition;

		StackedRecipe(IRecipe<?> recipe, int count, BooleanSupplier clearCondition) {
			this.recipe = recipe;
			this.count = count;
			this.clearCondition = clearCondition;
		}

	}

}
