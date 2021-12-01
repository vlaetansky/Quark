package vazkii.quark.content.client.render.variant;

import net.minecraft.client.renderer.entity.CowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.resources.ResourceLocation;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule.VariantTextureType;

public class VariantCowRenderer extends CowRenderer {

	public VariantCowRenderer(EntityRendererProvider.Context context) {
		super(context);
	}
	
	@Override
	public ResourceLocation getTextureLocation(Cow entity) {
		return VariantAnimalTexturesModule.getTextureOrShiny(entity, VariantTextureType.COW, VariantAnimalTexturesModule.enableCow);
	}
	
}
