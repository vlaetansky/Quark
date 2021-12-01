package vazkii.quark.content.client.render.variant;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.RabbitRenderer;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.resources.ResourceLocation;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule.VariantTextureType;

public class VariantRabbitRenderer extends RabbitRenderer {

	public VariantRabbitRenderer(EntityRenderDispatcher renderManagerIn) {
		super(renderManagerIn);
	}
	
	@Override
	public ResourceLocation getTextureLocation(Rabbit entity) {
		return VariantAnimalTexturesModule.getTextureOrShiny(entity, VariantTextureType.RABBIT, () -> super.getTextureLocation(entity));
	}

}
