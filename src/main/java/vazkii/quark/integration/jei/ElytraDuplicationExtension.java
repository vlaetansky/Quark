package vazkii.quark.integration.jei;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import vazkii.quark.content.tweaks.recipe.ElytraDuplicationRecipe;

public class ElytraDuplicationExtension implements ICraftingCategoryExtension {
	private final ElytraDuplicationRecipe recipe;

	ElytraDuplicationExtension(ElytraDuplicationRecipe recipe) {
		this.recipe = recipe;
	}

	@Override
	public void setIngredients(IIngredients ingredients) {
		ingredients.setInputIngredients(recipe.getIngredients());
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
	}

	@Override
	public void drawInfo(int recipeWidth, int recipeHeight, PoseStack poseStack, double mouseX, double mouseY) {
		Minecraft.getInstance().font.draw(poseStack, I18n.get("quark.jei.makes_copy"), 60, 46, 0x555555);
	}

	@Nullable
	@Override
	public ResourceLocation getRegistryName() {
		return recipe.getId();
	}
}
