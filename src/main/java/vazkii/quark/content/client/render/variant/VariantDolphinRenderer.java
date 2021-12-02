package vazkii.quark.content.client.render.variant;

import net.minecraft.client.renderer.entity.DolphinRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Dolphin;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule.VariantTextureType;

public class VariantDolphinRenderer extends DolphinRenderer {

	public VariantDolphinRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public ResourceLocation getTextureLocation(Dolphin entity) {
		return VariantAnimalTexturesModule.getTextureOrShiny(entity, VariantTextureType.DOLPHIN, () -> super.getTextureLocation(entity));
	}
	
}
