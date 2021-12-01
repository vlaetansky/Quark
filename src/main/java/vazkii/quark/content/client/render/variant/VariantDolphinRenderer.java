package vazkii.quark.content.client.render.variant;

import net.minecraft.client.renderer.entity.DolphinRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.resources.ResourceLocation;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule.VariantTextureType;

public class VariantDolphinRenderer extends DolphinRenderer {

	public VariantDolphinRenderer(EntityRenderDispatcher renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	public ResourceLocation getTextureLocation(Dolphin entity) {
		return VariantAnimalTexturesModule.getTextureOrShiny(entity, VariantTextureType.DOLPHIN, () -> super.getTextureLocation(entity));
	}
	
}
