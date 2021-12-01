package vazkii.quark.content.building.client.render;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import vazkii.quark.content.building.entity.StoolEntity;

public class StoolEntityRenderer extends EntityRenderer<StoolEntity> {

	public StoolEntityRenderer(EntityRenderDispatcher renderManager) {
		super(renderManager);
	}

	@Override
	public ResourceLocation getTextureLocation(StoolEntity entity) {
		return null;
	}
	
	@Override
	public boolean shouldRender(StoolEntity livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
		return false;
	}
	
}
