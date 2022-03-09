package vazkii.quark.integration.jei;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import vazkii.quark.content.tweaks.recipe.ElytraDuplicationRecipe;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ElytraDuplicationExtension implements ICraftingCategoryExtension {
	private final ElytraDuplicationRecipe recipe;

	ElytraDuplicationExtension(ElytraDuplicationRecipe recipe) {
		this.recipe = recipe;
	}

	@Override
	public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull ICraftingGridHelper craftingGridHelper, @NotNull IFocusGroup focuses) {
		List<List<ItemStack>> inputLists = new ArrayList<>();
		for (Ingredient input : recipe.getIngredients()) {
			ItemStack[] stacks = input.getItems();
			List<ItemStack> expandedInput = List.of(stacks);
			inputLists.add(expandedInput);
		}
		craftingGridHelper.setInputs(builder, VanillaTypes.ITEM, inputLists, 0, 0);
		craftingGridHelper.setOutputs(builder, VanillaTypes.ITEM, Lists.newArrayList(recipe.getResultItem()));

	}

	@Override
	public void drawInfo(int recipeWidth, int recipeHeight, @NotNull PoseStack poseStack, double mouseX, double mouseY) {
		Minecraft.getInstance().font.draw(poseStack, I18n.get("quark.jei.makes_copy"), 60, 46, 0x555555);
	}

	@Nullable
	@Override
	public ResourceLocation getRegistryName() {
		return recipe.getId();
	}
}
