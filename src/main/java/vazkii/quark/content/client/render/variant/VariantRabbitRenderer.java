package vazkii.quark.content.client.render.variant;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RabbitRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Rabbit;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule.VariantTextureType;

import javax.annotation.Nonnull;

public class VariantRabbitRenderer extends RabbitRenderer {

	public VariantRabbitRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Nonnull
	@Override
	public ResourceLocation getTextureLocation(@Nonnull Rabbit entity) {
		return VariantAnimalTexturesModule.getTextureOrShiny(entity, VariantTextureType.RABBIT, () -> super.getTextureLocation(entity));
	}

}
