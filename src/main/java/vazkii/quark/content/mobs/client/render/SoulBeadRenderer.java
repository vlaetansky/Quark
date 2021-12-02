package vazkii.quark.content.mobs.client.render;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import vazkii.quark.content.mobs.entity.SoulBeadEntity;

public class SoulBeadRenderer extends EntityRenderer<SoulBeadEntity> {

	public SoulBeadRenderer(EntityRendererProvider.Context p_174409_) {
		super(p_174409_);
	}

	@Override
	public ResourceLocation getTextureLocation(SoulBeadEntity entity) {
		return null;
	}
	
	@Override
	public boolean shouldRender(SoulBeadEntity livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
		return false;
	}
	
}