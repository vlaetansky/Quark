package vazkii.quark.content.mobs.client.model;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import vazkii.quark.content.mobs.entity.FrogEntity;

public class FrogModel extends EntityModel<FrogEntity> {

	private float frogSize;

	public final ModelPart headTop;
	public final ModelPart headBottom;
	public final ModelPart body;
	public final ModelPart rightArm;
	public final ModelPart leftArm;
	public final ModelPart rightEye;
	public final ModelPart leftEye;

	public FrogModel() {
		texWidth = 64;
		texHeight = 32;
		rightArm = new ModelPart(this, 33, 7);
		rightArm.mirror = true;
		rightArm.setPos(6.5F, 22.0F, 1.0F);
		rightArm.addBox(-1.0F, -1.0F, -5.0F, 3, 3, 6, 0.0F);
		leftArm = new ModelPart(this, 33, 7);
		leftArm.setPos(-6.5F, 22.0F, 1.0F);
		leftArm.addBox(-2.0F, -1.0F, -5.0F, 3, 3, 6, 0.0F);
		body = new ModelPart(this, 0, 7);
		body.setPos(0.0F, 20.0F, 0.0F);
		body.addBox(-5.5F, -3.0F, 0.0F, 11, 7, 11, 0.0F);
		headTop = new ModelPart(this, 0, 0);
		headTop.setPos(0.0F, 18.0F, 0.0F);
		headTop.addBox(-5.5F, -1.0F, -5.0F, 11, 2, 5, 0.0F);
		headBottom = new ModelPart(this, 32, 0);
		headBottom.setPos(0.0F, 18.0F, 0.0F);
		headBottom.addBox(-5.5F, 1.0F, -5.0F, 11, 2, 5, 0.0F);
		rightEye = new ModelPart(this, 0, 0);
		rightEye.mirror = true;
		rightEye.setPos(0.0F, 18.0F, 0.0F);
		rightEye.addBox(1.5F, -1.5F, -4.0F, 1, 1, 1, 0.0F);
		leftEye = new ModelPart(this, 0, 0);
		leftEye.setPos(0.0F, 18.0F, 0.0F);
		leftEye.addBox(-2.5F, -1.5F, -4.0F, 1, 1, 1, 0.0F);
	}


	@Override
	public void prepareMobModel(FrogEntity frog, float limbSwing, float limbSwingAmount, float partialTickTime) {
		int rawTalkTime = frog.getTalkTime();

		headBottom.xRot = (float) Math.PI / 120;

		if (rawTalkTime != 0) {
			float talkTime = rawTalkTime - partialTickTime;

			int speed = 10;

			headBottom.xRot += Math.PI / 8 * (1 - Mth.cos(talkTime * (float) Math.PI * 2 / speed));
		}
	}

	@Override
	public void setupAnim(FrogEntity frog, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		frogSize = frog.getSizeModifier();

		rightArm.xRot = Mth.cos(limbSwing * 2 / 3) * 1F * limbSwingAmount;
		leftArm.xRot = Mth.cos(limbSwing * 2 / 3) * 1F * limbSwingAmount;

		headTop.xRot = headPitch * (float) Math.PI / 180;
		rightEye.xRot = leftEye.xRot = headTop.xRot;
		headBottom.xRot += headPitch * (float) Math.PI / 180;

		if (frog.isVoid()) {
			headTop.xRot *= -1;
			rightEye.xRot *= -1;
			leftEye.xRot *= -1;
			headBottom.xRot *= -1;
		}
	}

	@Override
	public void renderToBuffer(PoseStack matrix, @Nonnull VertexConsumer vb, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		matrix.pushPose();
		matrix.translate(0, 1.5 - frogSize * 1.5, 0);
		matrix.scale(frogSize, frogSize, frogSize);

		if (young) {
			matrix.pushPose();
			matrix.translate(0, 0.6, 0);
			matrix.scale(0.625F, 0.625F, 0.625F);
		}

		headTop.render(matrix, vb, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		headBottom.render(matrix, vb, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		rightEye.render(matrix, vb, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		leftEye.render(matrix, vb, packedLightIn, packedOverlayIn, red, green, blue, alpha);

		if (young) {
			matrix.popPose();
			matrix.scale(0.5F, 0.5F, 0.5F);
			matrix.translate(0, 1.5, 0);
		}

		rightArm.render(matrix, vb, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		leftArm.render(matrix, vb, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		body.render(matrix, vb, packedLightIn, packedOverlayIn, red, green, blue, alpha);

		matrix.popPose();
	}

}
