package vazkii.quark.content.experimental.shiba.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.quark.content.experimental.shiba.entity.ShibaEntity;

public class ShibaModel extends EntityModel<ShibaEntity> {
	
	private final ModelPart main;

	private final ModelPart head;
	private final ModelPart rEar;
	private final ModelPart lEar;
	
	private final ModelPart tongue;
	private final ModelPart torso;
	private final ModelPart tail;
	
	private final ModelPart rFrontLeg;
	private final ModelPart lFrontLeg;
	private final ModelPart rBackLeg;
	private final ModelPart lBackLeg;
	
	private ShibaEntity entity;
	
	public ShibaModel() {
		texWidth = 80;
		texHeight = 48;
		
		main = new ModelPart(this);

		head = new ModelPart(this);
		head.setPos(0.0F, 15.0F, -5.0F);
		head.texOffs(16, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 11.0F, 6.0F, 0.0F, false);
		head.texOffs(44, 0).addBox(-1.5F, -6.0F, -8.0F, 3.0F, 3.0F, 4.0F, 0.0F, false);

		rEar = new ModelPart(this);
		rEar.setPos(3.0F, -12.0F, 2.0F);
		head.addChild(rEar);
		rEar.texOffs(0, 0).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 2.0F, 3.0F, 0.0F, false);

		lEar = new ModelPart(this);
		lEar.setPos(-3.0F, -12.0F, 2.0F);
		head.addChild(lEar);
		lEar.texOffs(0, 0).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 2.0F, 3.0F, 0.0F, true);

		tongue = new ModelPart(this);
		tongue.setPos(0.0F, -4.0F, -8.0F);
		head.addChild(tongue);
		tongue.texOffs(36, 34).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 0.0F, 3.0F, 0.0F, false);
		
		torso = new ModelPart(this);
		torso.setPos(0.0F, 13.0F, -7.0F);
		torso.texOffs(36, 10).addBox(-3.0F, 0.0F, -4.0F, 6.0F, 14.0F, 8.0F, 0.0F, false);

		tail = new ModelPart(this);
		tail.setPos(0.0F, 14.0F, 4.0F);
		torso.addChild(tail);
		tail.texOffs(0, 32).addBox(-2.0F, -3.0F, -3.0F, 4.0F, 6.0F, 6.0F, 0.0F, false);

		rFrontLeg = new ModelPart(this);
		rFrontLeg.setPos(3.0F, 16.0F, -5.0F);
		rFrontLeg.texOffs(0, 21).addBox(-2.0F, 0.0F, -1.0F, 3.0F, 8.0F, 3.0F, 0.0F, false);

		lFrontLeg = new ModelPart(this);
		lFrontLeg.setPos(-3.0F, 16.0F, -5.0F);
		lFrontLeg.texOffs(0, 21).addBox(-1.0F, 0.0F, -1.0F, 3.0F, 8.0F, 3.0F, 0.0F, true);

		rBackLeg = new ModelPart(this);
		rBackLeg.setPos(3.0F, 15.0F, 4.0F);
		rBackLeg.texOffs(12, 18).addBox(-2.0F, -1.0F, -2.0F, 3.0F, 10.0F, 4.0F, 0.0F, false);

		lBackLeg = new ModelPart(this);
		lBackLeg.setPos(-3.0F, 15.0F, 4.0F);
		lBackLeg.texOffs(12, 18).addBox(-1.0F, -1.0F, -2.0F, 3.0F, 10.0F, 4.0F, 0.0F, true);
		
		main.addChild(head);
		main.addChild(torso);
		main.addChild(rFrontLeg);
		main.addChild(rBackLeg);
		main.addChild(lFrontLeg);
		main.addChild(lBackLeg);
	}
	
	public void transformToHead(PoseStack matrix) {
		head.translateAndRotate(matrix);
	}

	@Override
	public void prepareMobModel(ShibaEntity shiba, float limbSwing, float limbSwingAmount, float partialTickTime) {
		this.entity = shiba;

		setRotationAngle(rFrontLeg, Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount, 0, 0);
		setRotationAngle(lFrontLeg, Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount, 0, 0);
		setRotationAngle(rBackLeg, Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount, 0, 0);
		setRotationAngle(lBackLeg, Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount, 0, 0);
	}
	
	@Override
	public void setupAnim(ShibaEntity shiba, float limbSwing, float limbSwingAmount, float ageInTicks, float yaw, float pitch) {
		main.setPos(0F, 0F, 0F);
		lBackLeg.setPos(-3.0F, 15.0F, 4.0F);
		rBackLeg.setPos(3.0F, 15.0F, 4.0F);
		
		setRotationAngle(main, 0, 0F, 0F);
		setRotationAngle(torso, 1.5708F, 0F, 0F);

		setRotationAngle(head, Mth.cos(ageInTicks * 0.6F) * 0.01F, yaw * 0.017453292F, Mth.sin(ageInTicks * 0.06F) * 0.06F);
		
		setRotationAngle(tail, Mth.cos(ageInTicks * 0.1F) * 0.1F, Mth.sin(ageInTicks * 0.15F) * 0.12F, Mth.cos(ageInTicks * 0.3F) * 0.2F);
		setRotationAngle(lEar, 0F, Mth.cos(ageInTicks * 0.08F) * 0.05F - 0.05F, 0F);
		setRotationAngle(rEar, 0F, Mth.sin(ageInTicks * 0.07F) * 0.05F + 0.05F, 0F);
		
		boolean tongueOut = false;
		
		BlockState state = shiba.getFeetBlockState();
		boolean sleep = state.is(BlockTags.BEDS);

		if(shiba.isSleeping()) {
			tongueOut = true;

			if(sleep) {
				main.setPos(16F, 18.0F, 0F);
				setRotationAngle(main, 0F, 0F, 1.5708F);
				
				setRotationAngle(lBackLeg, Mth.cos(ageInTicks * 0.2F) * 0.1F, 0F, Mth.sin(ageInTicks * 0.18F) * 0.12F);
				setRotationAngle(rBackLeg, Mth.sin(ageInTicks * 0.22F) * 0.08F, 0F, Mth.cos(ageInTicks * 0.16F) * 0.11F);
				
				setRotationAngle(rFrontLeg, Mth.cos(ageInTicks * 0.19F) * 0.1F, 0F, Mth.sin(ageInTicks * 0.21F) * 0.12F);
				setRotationAngle(lFrontLeg, Mth.sin(ageInTicks * 0.18F) * 0.08F, 0F, Mth.cos(ageInTicks * 0.2F) * 0.11F);
			} else {
				setRotationAngle(torso, 1F, 0F, 0F);
				
				lBackLeg.setPos(-3.0F, 19.0F, 2.0F);
				rBackLeg.setPos(3.0F, 19.0F, 2.0F);

				setRotationAngle(lBackLeg, -1F, -0.5F, 0F);
				setRotationAngle(rBackLeg, -1F, -0.5F, 0F);
				
				setRotationAngle(lFrontLeg, -0.5F, 0.5F, 0F);
				setRotationAngle(rFrontLeg, -0.5F, 0.5F, 0F);
			}
		}
		
		if(tongueOut && shiba.getMouthItem().isEmpty()) {
			tongue.setPos(0F, -4F, -6.75F + Mth.cos(ageInTicks * 0.19F) * 0.25F);
			setRotationAngle(tongue, Mth.cos(ageInTicks * 0.19F) * 0.1F + 0.2F, 0, 0);
		} else {
			tongue.setPos(0F, -4F, -5F);
			setRotationAngle(tongue, 0, 0, 0);
		}
	}

	@Override
	public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		matrixStack.pushPose();
		if(entity.isSleeping())
			matrixStack.translate(0, 0.12, 0);
		
		main.translateAndRotate(matrixStack);
		
		matrixStack.pushPose();
		if(young)
			matrixStack.translate(0.0F, 5.0F / 16F, 0F);
		
		head.render(matrixStack, buffer, packedLight, packedOverlay);
		matrixStack.popPose();

		matrixStack.pushPose();
		if (young) {
			matrixStack.translate(0.0F, 12.0F / 16F, 0F);
			matrixStack.scale(0.5F, 0.5F, 0.5F);
		}
		
		torso.render(matrixStack, buffer, packedLight, packedOverlay);
		rFrontLeg.render(matrixStack, buffer, packedLight, packedOverlay);
		rBackLeg.render(matrixStack, buffer, packedLight, packedOverlay);
		lFrontLeg.render(matrixStack, buffer, packedLight, packedOverlay);
		lBackLeg.render(matrixStack, buffer, packedLight, packedOverlay);
		matrixStack.popPose();
		matrixStack.popPose();
	}

	public void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}

}
