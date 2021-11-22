package vazkii.quark.content.management.module;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.util.function.BooleanSupplier;

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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseClickedEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
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
			Slot slot = cscreen.getSlotUnderMouse();
			
			if(recipeBook != null && slot != null) {
				GhostRecipe ghost = ObfuscationReflectionHelper.getPrivateValue(RecipeBookGui.class, recipeBook, "ghostRecipe"); // TODO AT
				if(ghost != null && ghost.getRecipe() != null) {
					for(int i = 1; i < ghost.size(); i++) { // start at 1 to skip output
						GhostIngredient ingr = ghost.get(i);
						
						if(ingr.getX() == slot.xPos && ingr.getY() == slot.yPos) {
							ItemStack stack = ingr.getItem();
							
							TextFieldWidget text = ObfuscationReflectionHelper.getPrivateValue(RecipeBookGui.class, recipeBook, "searchBar"); // TODO AT
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
								if(recipeLists != null && recipeLists.size() > 0) {
									IRecipe<?> recipeToSet = null;
									
									for(RecipeList list : recipeLists) {
										List<IRecipe<?>> recipeList = list.getDisplayRecipes(true); // check craftable first
										if(recipeList != null && !recipeList.isEmpty() && recipeList.get(0).getRecipeOutput().getItem() == stack.getItem()) {
											recipeToSet = recipeList.get(0);
											break;
										}
										
										recipeList = list.getDisplayRecipes(false); // check again for not craftable
										if(recipeList != null && !recipeList.isEmpty() && recipeList.get(0).getRecipeOutput().getItem() == stack.getItem()) {
											recipeToSet = recipeList.get(0);
											break;
										}
									}
									
									if(recipeToSet != null) {
										int ourCount = 0;
										
										for(int j = 1; j < ghost.size(); j++) { // start at 1 to skip output
											GhostIngredient testIngr = ghost.get(j);
											ItemStack testStack = testIngr.getItem();
											if(ItemStack.areItemsEqual(testStack, stack)) // TODO NBT sensitivity?
												ourCount++;
										}
										
										if(ourCount > 0) {
											int prevCount = compoundCount;
											compoundCount *= ourCount;
											
											ItemStack clearStack = stack.copy();
											BooleanSupplier clearCond = () -> {
												int missing = compoundCount;
												for(ItemStack invStack : mc.player.inventory.mainInventory) {
													if(ItemStack.areItemsEqual(invStack, clearStack)) {
														missing -= invStack.getCount();
														
														if(missing <= 0)
															return true;
													}
												}
												
												return false;
											};
											
											StackedRecipe stackedRecipe = new StackedRecipe(ghost.getRecipe(), prevCount, clearCond);
											recipes.add(stackedRecipe);
											
											ghost.clear();
											
											mc.playerController.sendPlaceRecipePacket(mc.player.openContainer.windowId, recipeToSet, true);
										}
									}
								}
							}
							
							event.setCanceled(true);
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onDrawGui(DrawScreenEvent.Post event) {
		if(!recipes.isEmpty()) {
			Minecraft mc = Minecraft.getInstance();
			
			Screen screen = mc.currentScreen;
			if(screen instanceof ContainerScreen<?>) {
				ContainerScreen<?> cscreen = (ContainerScreen<?>) screen;
				MatrixStack mstack = event.getMatrixStack();
				ItemRenderer render = mc.getItemRenderer();
				int left = cscreen.getGuiLeft() + 90;
				int top = cscreen.getGuiTop() + 6;
				
				for(int i = 0; i < recipes.size(); i++) {
					StackedRecipe recipe = recipes.get(i);
					int x = left + i * 22;
					int y = top;
					
					ItemStack copy = recipe.recipe.getRecipeOutput().copy();
					copy.setCount(recipe.count);
					render.renderItemIntoGUI(copy, x, y);
					
					if(i > 0)
						mc.fontRenderer.drawString(mstack, ">", x - 5, y + 5, 0x3f3f3f);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		Minecraft mc = Minecraft.getInstance();
		Screen prevScreen = currentScreen;
		currentScreen = mc.currentScreen;
		
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
	
	private static class StackedRecipe {
		
		public final IRecipe recipe;
		public final int count;
		public final BooleanSupplier clearCondition;
		
		StackedRecipe(IRecipe<?> recipe, int count, BooleanSupplier clearCondition) {
			this.recipe = recipe;
			this.count = count;
			this.clearCondition = clearCondition;
		}
		
	}
	
}
