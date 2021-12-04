package vazkii.quark.content.building.client.render.entity;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import vazkii.quark.content.building.entity.Stool;

public class StoolEntityRenderer extends EntityRenderer<Stool> {

	public StoolEntityRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public ResourceLocation getTextureLocation(Stool entity) {
		return null;
	}
	
	@Override
	public boolean shouldRender(Stool livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
		return false;
	}
	
}
