package vazkii.quark.content.mobs.client.render.entity;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import vazkii.quark.content.mobs.entity.SoulBead;

public class SoulBeadRenderer extends EntityRenderer<SoulBead> {

	public SoulBeadRenderer(EntityRendererProvider.Context p_174409_) {
		super(p_174409_);
	}

	@Override
	public ResourceLocation getTextureLocation(SoulBead entity) {
		return null;
	}
	
	@Override
	public boolean shouldRender(SoulBead livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
		return false;
	}
	
}