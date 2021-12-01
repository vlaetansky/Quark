package vazkii.quark.content.tools.client.render;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import vazkii.quark.content.tools.entity.PickarangEntity;

public class PickarangRenderer extends EntityRenderer<PickarangEntity> {

	public PickarangRenderer(EntityRenderDispatcher renderManager) {
		super(renderManager);
	}
	
	@Override
	public void render(PickarangEntity entity, float yaw, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int light) {
		matrix.pushPose();
		matrix.translate(0, 0.2, 0);
		matrix.mulPose(Vector3f.XP.rotationDegrees(90F));
		
		Minecraft mc = Minecraft.getInstance();
		float time = entity.tickCount + (mc.isPaused() ? 0 : partialTicks);
		matrix.mulPose(Vector3f.ZP.rotationDegrees(time * 20F));

		mc.getItemRenderer().renderStatic(entity.getStack(), TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, matrix, buffer);
		
		matrix.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(@Nonnull PickarangEntity entity) {
		return null;
	}

}
