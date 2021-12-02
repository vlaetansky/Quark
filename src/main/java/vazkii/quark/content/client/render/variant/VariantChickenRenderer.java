package vazkii.quark.content.client.render.variant;

import net.minecraft.client.renderer.entity.ChickenRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Chicken;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule.VariantTextureType;

public class VariantChickenRenderer extends ChickenRenderer {

	public VariantChickenRenderer(EntityRendererProvider.Context context) {
		super(context);
	}
	@Override
	public ResourceLocation getTextureLocation(Chicken entity) {
		return VariantAnimalTexturesModule.getTextureOrShiny(entity, VariantTextureType.CHICKEN, VariantAnimalTexturesModule.enableChicken);
	}
	
}
