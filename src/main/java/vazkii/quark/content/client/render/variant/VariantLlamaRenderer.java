package vazkii.quark.content.client.render.variant;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LlamaRenderer;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.resources.ResourceLocation;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule.VariantTextureType;

public class VariantLlamaRenderer extends LlamaRenderer {

	public VariantLlamaRenderer(EntityRenderDispatcher renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	public ResourceLocation getTextureLocation(Llama entity) {
		return VariantAnimalTexturesModule.getTextureOrShiny(entity, VariantTextureType.LLAMA, () -> super.getTextureLocation(entity));
	}
	
}
