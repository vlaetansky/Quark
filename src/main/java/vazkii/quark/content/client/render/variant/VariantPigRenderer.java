package vazkii.quark.content.client.render.variant;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Pig;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule.VariantTextureType;

public class VariantPigRenderer extends PigRenderer {

	public VariantPigRenderer(EntityRendererProvider.Context context) {
		super(context);
	}
	
	@Override
	public ResourceLocation getTextureLocation(Pig entity) {
		return VariantAnimalTexturesModule.getTextureOrShiny(entity, VariantTextureType.PIG, VariantAnimalTexturesModule.enablePig);
	}

}
