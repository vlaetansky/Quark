package vazkii.quark.content.client.render.variant;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.resources.ResourceLocation;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule.VariantTextureType;

public class VariantPigRenderer extends PigRenderer {

	public VariantPigRenderer(EntityRenderDispatcher renderManagerIn) {
		super(renderManagerIn);
	}
	
	@Override
	public ResourceLocation getTextureLocation(Pig entity) {
		return VariantAnimalTexturesModule.getTextureOrShiny(entity, VariantTextureType.PIG, VariantAnimalTexturesModule.enablePig);
	}

}
