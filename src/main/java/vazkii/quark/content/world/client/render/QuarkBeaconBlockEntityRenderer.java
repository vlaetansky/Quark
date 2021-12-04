package vazkii.quark.content.world.client.render;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import vazkii.quark.content.world.module.underground.CaveCrystalUndergroundBiomeModule;
import vazkii.quark.content.world.module.underground.CaveCrystalUndergroundBiomeModule.ExtendedBeamSegment;

// Mostly vanilla copypaste but adapted to use ExtendedBeamSegment values
public class QuarkBeaconBlockEntityRenderer {

	public static boolean render(BeaconBlockEntity tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
		if (!CaveCrystalUndergroundBiomeModule.staticEnabled || !CaveCrystalUndergroundBiomeModule.enableBeaconRedirection)
			return false;

		long i = tileEntityIn.getLevel().getGameTime();
		List<BeaconBlockEntity.BeaconBeamSection> list = tileEntityIn.getBeamSections();

		for(int k = 0; k < list.size(); ++k) {
			BeaconBlockEntity.BeaconBeamSection segment = list.get(k);
			if(!(segment instanceof ExtendedBeamSegment))
				return false; // Defer back to the vanilla one
			
			renderBeamSegment(matrixStackIn, bufferIn, (ExtendedBeamSegment) segment, partialTicks, i);
		}

		return true;
	}

	private static void renderBeamSegment(PoseStack matrixStackIn, MultiBufferSource bufferIn, ExtendedBeamSegment segment, float partialTicks, long totalWorldTime) {
		renderBeamSegment(matrixStackIn, bufferIn, BeaconRenderer.BEAM_LOCATION, segment, partialTicks, 1.0F, totalWorldTime, 0.2F, 0.25F);
	}

	public static void renderBeamSegment(PoseStack matrixStackIn, MultiBufferSource bufferIn, ResourceLocation textureLocation, ExtendedBeamSegment segment, float partialTicks, float textureScale, long totalWorldTime, float beamRadius, float glowRadius) {
		int height = segment.getHeight();
		float[] colors = segment.getColor();
		
		matrixStackIn.pushPose();
		matrixStackIn.translate(0.5D, 0.5D, 0.5D); // Y translation changed to 0.5 
		matrixStackIn.translate(segment.offset.getX(), segment.offset.getY(), segment.offset.getZ()); // offset by the correct distance
		matrixStackIn.mulPose(segment.dir.getRotation());
		
		float f = -(Math.floorMod(totalWorldTime, 40L) + partialTicks);
		float f2 = Mth.frac(1 * 0.2F - (float)Mth.floor(f * 0.1F));
		float f3 = colors[0];
		float f4 = colors[1];
		float f5 = colors[2];
		
		matrixStackIn.pushPose();
		matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(f * 2.25F - 45.0F));
		float f6 = 0.0F;
		float f8 = 0.0F;
		float f9 = -beamRadius;
		float f12 = -beamRadius;
		float f15 = -1.0F + f2;
		float f16 = (float)height * textureScale * (0.5F / beamRadius) + f15;
		renderPart(matrixStackIn, bufferIn.getBuffer(RenderType.beaconBeam(textureLocation, false)), f3, f4, f5, 1.0F, height, 0.0F, beamRadius, beamRadius, 0.0F, f9, 0.0F, 0.0F, f12, 0.0F, 1.0F, f16, f15);
		matrixStackIn.popPose();
		f6 = -glowRadius;
		float f7 = -glowRadius;
		f8 = -glowRadius;
		f9 = -glowRadius;
		f15 = -1.0F + f2;
		f16 = (float)height * textureScale + f15;
		renderPart(matrixStackIn, bufferIn.getBuffer(RenderType.beaconBeam(textureLocation, true)), f3, f4, f5, 0.125F, height, f6, f7, glowRadius, f8, f9, glowRadius, glowRadius, glowRadius, 0.0F, 1.0F, f16, f15);
		matrixStackIn.popPose();
	}

	private static void renderPart(PoseStack matrixStackIn, VertexConsumer bufferIn, float red, float green, float blue, float alpha, int height, float p_228840_8_, float p_228840_9_, float p_228840_10_, float p_228840_11_, float p_228840_12_, float p_228840_13_, float p_228840_14_, float p_228840_15_, float u1, float u2, float v1, float v2) {
		PoseStack.Pose matrixstack$entry = matrixStackIn.last();
		Matrix4f matrix4f = matrixstack$entry.pose();
		Matrix3f matrix3f = matrixstack$entry.normal();
		addQuad(matrix4f, matrix3f, bufferIn, red, green, blue, alpha, 0, height, p_228840_8_, p_228840_9_, p_228840_10_, p_228840_11_, u1, u2, v1, v2);
		addQuad(matrix4f, matrix3f, bufferIn, red, green, blue, alpha, 0, height, p_228840_14_, p_228840_15_, p_228840_12_, p_228840_13_, u1, u2, v1, v2);
		addQuad(matrix4f, matrix3f, bufferIn, red, green, blue, alpha, 0, height, p_228840_10_, p_228840_11_, p_228840_14_, p_228840_15_, u1, u2, v1, v2);
		addQuad(matrix4f, matrix3f, bufferIn, red, green, blue, alpha, 0, height, p_228840_12_, p_228840_13_, p_228840_8_, p_228840_9_, u1, u2, v1, v2);
	}

	private static void addQuad(Matrix4f matrixPos, Matrix3f matrixNormal, VertexConsumer bufferIn, float red, float green, float blue, float alpha, int yMin, int yMax, float x1, float z1, float x2, float z2, float u1, float u2, float v1, float v2) {
		addVertex(matrixPos, matrixNormal, bufferIn, red, green, blue, alpha, yMax, x1, z1, u2, v1);
		addVertex(matrixPos, matrixNormal, bufferIn, red, green, blue, alpha, yMin, x1, z1, u2, v2);
		addVertex(matrixPos, matrixNormal, bufferIn, red, green, blue, alpha, yMin, x2, z2, u1, v2);
		addVertex(matrixPos, matrixNormal, bufferIn, red, green, blue, alpha, yMax, x2, z2, u1, v1);
	}

	private static void addVertex(Matrix4f matrixPos, Matrix3f matrixNormal, VertexConsumer bufferIn, float red, float green, float blue, float alpha, int y, float x, float z, float texU, float texV) {
		bufferIn.vertex(matrixPos, x, (float)y, z).color(red, green, blue, alpha).uv(texU, texV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(matrixNormal, 0.0F, 1.0F, 0.0F).endVertex();
	}

}
