package vazkii.quark.content.client.render.variant;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LlamaRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Llama;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule.VariantTextureType;

public class VariantLlamaRenderer extends LlamaRenderer {

	public VariantLlamaRenderer(EntityRendererProvider.Context context) {
		super(context, ModelLayers.LLAMA);
	}

	@Override
	public ResourceLocation getTextureLocation(Llama entity) {
		return VariantAnimalTexturesModule.getTextureOrShiny(entity, VariantTextureType.LLAMA, () -> super.getTextureLocation(entity));
	}
	
}
