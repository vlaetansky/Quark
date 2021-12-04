package vazkii.quark.content.mobs.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import vazkii.quark.content.mobs.entity.Stoneling;

public class StonelingModel extends EntityModel<Stoneling> {

	private final ModelPart body;
	private final ModelPart arm_right;
	private final ModelPart arm_left;
	private final ModelPart leg_right;
	private final ModelPart leg_left;

	public StonelingModel(ModelPart root) {
		body = root.getChild("body");
		arm_right = body.getChild("arm_right");
		arm_left = body.getChild("arm_left");
		leg_right = body.getChild("leg_right");
		leg_left = body.getChild("leg_left");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();

		PartDefinition body = root.addOrReplaceChild("body", 
				CubeListBuilder.create(),
				PartPose.offset(0.0F, 14.0F, 0.0F));

		body.addOrReplaceChild("head", 
				CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-3.0F, -2.0F, -3.0F, 6, 8, 6)
				.texOffs(8, 24)
				.addBox(-1.0F, -4.0F, -5.0F, 2, 4, 2)
				.texOffs(16, 20)
				.addBox(-1.0F, 6.0F, -3.0F, 2, 2, 2)
				.texOffs(0, 24)
				.addBox(-1.0F, -4.0F, 3.0F, 2, 4, 2)
				.texOffs(16, 24)
				.addBox(-1.0F, -4.0F, -3.0F, 2, 2, 6)
				.texOffs(24, 20)
				.addBox(-1.0F, -4.0F, -1.0F, 2, 2, 2)
				.texOffs(18, 0)
				.addBox(-1.0F, 1.0F, -5.0F, 2, 2, 2)
				.texOffs(0, 0)
				.addBox(-4.0F, -1.0F, -3.0F, 1, 2, 2)
				.texOffs(0, 0)
				.addBox(3.0F, -1.0F, -3.0F, 1, 2, 2),
				PartPose.ZERO);
		
		body.addOrReplaceChild("arm_right",
			CubeListBuilder.create()
			.texOffs(0, 14)
			.addBox(-2.0F, 0.0F, -1.0F, 2, 8, 2),
		PartPose.offsetAndRotation(-3.0F, 2.0F, 0.0F, 3.1416F, 0.0F, 0.0F));
		
		body.addOrReplaceChild("arm_left",
			CubeListBuilder.create()
			.texOffs(8, 14)
			.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2),
		PartPose.offsetAndRotation(3.0F, 2.0F, 0.0F, 3.1416F, 0.0F, 0.0F));
		
		body.addOrReplaceChild("leg_right",
			CubeListBuilder.create()
			.texOffs(16, 14)
			.addBox(-1.0F, 2.0F, -1.0F, 2, 4, 2),
		PartPose.offsetAndRotation(-2.0F, 4.0F, 0.0F, 0.0F, 0.0F, 0.0F));

		body.addOrReplaceChild("leg_left",
			CubeListBuilder.create()
			.texOffs(24, 14)
			.addBox(0.0F, 2.0F, -1.0F, 2, 4, 2),
		PartPose.offsetAndRotation(1.0F, 4.0F, 0.0F, 0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(mesh, 32, 32);
	}

	@Override
	public void setupAnim(Stoneling stoneling, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		leg_right.xRot = Mth.cos(limbSwing * 0.6662F) * limbSwingAmount;
		leg_left.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * limbSwingAmount;

		ItemStack carry = stoneling.getCarryingItem();
		if(carry.isEmpty() && !stoneling.isVehicle()) {
			arm_right.xRot = 0F;
			arm_left.xRot = 0F;
		} else {
			arm_right.xRot = 3.1416F;
			arm_left.xRot = 3.1416F;
		}
	}

	@Override
	public void renderToBuffer(PoseStack matrix, VertexConsumer vb, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
		body.render(matrix, vb, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
	}

	public void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}
