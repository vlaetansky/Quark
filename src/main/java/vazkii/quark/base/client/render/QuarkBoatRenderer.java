package vazkii.quark.base.client.render;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import vazkii.quark.base.Quark;
import vazkii.quark.base.client.handler.ModelHandler;
import vazkii.quark.base.item.boat.QuarkBoat;

import javax.annotation.Nonnull;
import java.util.Map;

public class QuarkBoatRenderer extends EntityRenderer<QuarkBoat> {

	private final Map<String, Pair<ResourceLocation, BoatModel>> boatResources;

	public QuarkBoatRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.shadowRadius = 0.8F;
		boatResources = computeBoatResources(context);
	}

	private static Map<String, Pair<ResourceLocation, BoatModel>> computeBoatResources(EntityRendererProvider.Context context) {
		return QuarkBoat.boatTypes().collect(ImmutableMap.toImmutableMap(Functions.identity(), name -> {
			ResourceLocation texture = new ResourceLocation(Quark.MOD_ID, "textures/model/entity/boat/" + name + ".png");
			BoatModel model = new BoatModel(context.bakeLayer(ModelHandler.quark_boat));

			return Pair.of(texture, model);
		}));
	}

	// All BoatRenderer copy from here on out =====================================================================================================================

	@Override
	public void render(QuarkBoat boat, float yaw, float partialTicks, PoseStack matrix, @Nonnull MultiBufferSource buffer, int light) {
		matrix.pushPose();
		matrix.translate(0.0D, 0.375D, 0.0D);
		matrix.mulPose(Vector3f.YP.rotationDegrees(180.0F - yaw));
		float wiggleAngle = (float)boat.getHurtTime() - partialTicks;
		float wiggleMagnitude = boat.getDamage() - partialTicks;
		if (wiggleMagnitude < 0.0F) {
			wiggleMagnitude = 0.0F;
		}

		if (wiggleAngle > 0.0F) {
			matrix.mulPose(Vector3f.XP.rotationDegrees(Mth.sin(wiggleAngle) * wiggleAngle * wiggleMagnitude / 10.0F * (float)boat.getHurtDir()));
		}

		float f2 = boat.getBubbleAngle(partialTicks);
		if (!Mth.equal(f2, 0.0F)) {
			matrix.mulPose(new Quaternion(new Vector3f(1.0F, 0.0F, 1.0F), boat.getBubbleAngle(partialTicks), true));
		}

		Pair<ResourceLocation, BoatModel> pair = getModelWithLocation(boat);
		ResourceLocation loc = pair.getFirst();
		BoatModel model = pair.getSecond();
		matrix.scale(-1.0F, -1.0F, 1.0F);
		matrix.mulPose(Vector3f.YP.rotationDegrees(90.0F));
		model.setupAnim(boat, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
		VertexConsumer vertexconsumer = buffer.getBuffer(model.renderType(loc));
		model.renderToBuffer(matrix, vertexconsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		if (!boat.isUnderWater()) {
			VertexConsumer waterMask = buffer.getBuffer(RenderType.waterMask());
			model.waterPatch().render(matrix, waterMask, light, OverlayTexture.NO_OVERLAY);
		}

		matrix.popPose();
		super.render(boat, yaw, partialTicks, matrix, buffer, light);
	}

	@Nonnull
	@Override
	@Deprecated // forge: override getModelWithLocation to change the texture / model
	public ResourceLocation getTextureLocation(@Nonnull QuarkBoat boat) {
		return getModelWithLocation(boat).getFirst();
	}

	public Pair<ResourceLocation, BoatModel> getModelWithLocation(QuarkBoat boat) {
		return this.boatResources.get(boat.getQuarkBoatType());
	}

}
