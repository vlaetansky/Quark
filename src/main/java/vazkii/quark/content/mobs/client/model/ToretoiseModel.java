package vazkii.quark.content.mobs.client.model;

import java.util.function.BiConsumer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.Cube;
import com.mojang.math.Vector3f;
import vazkii.arl.util.ClientTicker;
import vazkii.quark.content.mobs.entity.ToretoiseEntity;

public class ToretoiseModel extends EntityModel<ToretoiseEntity> {
	
	private ToretoiseEntity entity;
    private float animFrames;
	
    public ModelPart body;
    public ModelPart head;
    public ModelPart rightFrontLeg;
    public ModelPart leftFrontLeg;
    public ModelPart rightBackLeg;
    public ModelPart leftBackLeg;
    public ModelPart mouth;
    
    public ModelPart CoalOre1;
    public ModelPart CoalOre2;
    public ModelPart CoalOre3;
    public ModelPart CoalOre4;
    public ModelPart IronOre1;
    public ModelPart IronOre2;
    public ModelPart IronOre3;
    public ModelPart LapisOre1;
    public ModelPart LapisOre2;
    public ModelPart LapisOre3;
    public ModelPart LapisOre4;
    public ModelPart RedstoneOre1;
    public ModelPart RedstoneOre2;
    public ModelPart RedstoneOre3;
    public ModelPart RedstoneOre4;
    public ModelPart RedstoneOre5;

    public ToretoiseModel() {
        texWidth = 100;
        texHeight = 100;
        mouth = new ModelPart(this, 66, 38);
        mouth.setPos(0.0F, 1.0F, -1.0F);
        mouth.addBox(-4.5F, -2.5F, -8.0F, 9, 4, 8, 0.0F);
        
        leftFrontLeg = new ModelPart(this, 34, 38);
        leftFrontLeg.mirror = true;
        leftFrontLeg.setPos(10.0F, 16.0F, -12.0F);
        leftFrontLeg.addBox(-4.0F, -2.0F, -4.0F, 8, 10, 8, 0.0F);
        setRotateAngle(leftFrontLeg, 0.0F, -0.7853981633974483F, 0.0F);
        
        rightBackLeg = new ModelPart(this, 34, 38);
        rightBackLeg.setPos(-10.0F, 16.0F, 12.0F);
        rightBackLeg.addBox(-4.0F, -2.0F, -4.0F, 8, 10, 8, 0.0F);
        setRotateAngle(rightBackLeg, 0.0F, 0.7853981633974483F, 0.0F);
        
        body = new ModelPart(this, 0, 0);
        body.setPos(0.0F, 8.0F, 0.0F);
        body.addBox(-11.0F, 0.0F, -13.0F, 22, 12, 26, 0.0F);
        
        head = new ModelPart(this, 0, 38);
        head.setPos(0.0F, 16.0F, -13.0F);
        head.addBox(-4.0F, -4.0F, -8.0F, 8, 5, 8, 0.0F);
        
        rightFrontLeg = new ModelPart(this, 34, 38);
        rightFrontLeg.setPos(-10.0F, 16.0F, -12.0F);
        rightFrontLeg.addBox(-4.0F, -2.0F, -4.0F, 8, 10, 8, 0.0F);
        setRotateAngle(rightFrontLeg, 0.0F, 0.7853981633974483F, 0.0F);
        
        leftBackLeg = new ModelPart(this, 34, 38);
        leftBackLeg.mirror = true;
        leftBackLeg.setPos(10.0F, 16.0F, 12.0F);
        leftBackLeg.addBox(-4.0F, -2.0F, -4.0F, 8, 10, 8, 0.0F);
        
        setRotateAngle(leftBackLeg, 0.0F, -0.7853981633974483F, 0.0F);
        head.addChild(mouth);
        
        CoalOre1 = new ModelPart(this, 36, 56);
        CoalOre1.addBox(0.0F, -7.0F, -6.0F, 3, 3, 3, 0.0F);
        CoalOre1.setPos(0.0F, 0.0F, 0.0F);
        CoalOre2 = new ModelPart(this, 42, 56);
        CoalOre2.addBox(7.0F, -2.0F, -10.0F, 6, 6, 6, 0.0F);
        CoalOre2.setPos(0.0F, 0.0F, 0.0F);
        CoalOre3 = new ModelPart(this, 66, 50);
        CoalOre3.addBox(-2.0F, -7.0F, -4.0F, 7, 7, 7, 0.0F);
        CoalOre3.setPos(0.0F, 0.0F, 0.0F);
        CoalOre4 = new ModelPart(this, 60, 64);
        CoalOre4.addBox(-15.0F, 0.0F, 1.0F, 4, 6, 6, 0.0F);
        CoalOre4.setPos(0.0F, 0.0F, 0.0F);
        
        IronOre1 = new ModelPart(this, 36, 89);
        IronOre1.addBox(1.0F, -3.0F, 1.0F, 8, 3, 8, 0.0F);
        IronOre1.setPos(0.0F, 0.0F, 0.0F);
        IronOre2 = new ModelPart(this, 32, 81);
        IronOre2.addBox(-7.0F, -2.0F, -11.0F, 6, 2, 6, 0.0F);
        IronOre2.setPos(0.0F, 0.0F, 0.0F);
        IronOre3 = new ModelPart(this, 30, 76);
        IronOre3.addBox(-9.0F, -1.0F, 6.0F, 4, 1, 4, 0.0F);
        IronOre3.setPos(0.0F, 0.0F, 0.0F);
        
        LapisOre1 = new ModelPart(this, 0, 51);
        LapisOre1.addBox(-5.0F, -8.0F, 0.0F, 8, 8, 0, 0.0F);
        LapisOre1.setPos(0.0F, 0.0F, 0.0F);
        LapisOre2 = new ModelPart(this, 0, 53);
        LapisOre2.addBox(-1.0F, -8.0F, -4.0F, 0, 8, 8, 0.0F);
        LapisOre2.setPos(0.0F, 0.0F, 0.0F);
        LapisOre3 = new ModelPart(this, 18, 51);
        LapisOre3.addBox(-10.0F, -8.0F, 8.0F, 8, 8, 0, 0.0F);
        LapisOre3.setPos(0.0F, 0.0F, 0.0F);
        LapisOre4 = new ModelPart(this, 18, 53);
        LapisOre4.addBox(-6.0F, -8.0F, 4.0F, 0, 8, 8, 0.0F);
        LapisOre4.setPos(0.0F, 0.0F, 0.0F);
        
        RedstoneOre1 = new ModelPart(this, 0, 83);
        RedstoneOre1.addBox(-8.0F, -12.0F, -6.0F, 5, 12, 5, 0.0F);
        RedstoneOre1.setPos(0.0F, 0.0F, 0.0F);
        RedstoneOre2 = new ModelPart(this, 0, 74);
        RedstoneOre2.addBox(6.0F, -6.0F, -1.0F, 3, 6, 3, 0.0F);
        RedstoneOre2.setPos(0.0F, 0.0F, 0.0F);
        RedstoneOre3 = new ModelPart(this, 12, 76);
        RedstoneOre3.addBox(-7.0F, -4.0F, 2.0F, 2, 4, 2, 0.0F);
        RedstoneOre3.setPos(0.0F, 0.0F, 0.0F);
        RedstoneOre4 = new ModelPart(this, 20, 87);
        RedstoneOre4.addBox(1.0F, -9.0F, -9.0F, 4, 9, 4, 0.0F);
        RedstoneOre4.setPos(0.0F, 0.0F, 0.0F);
        RedstoneOre5 = new ModelPart(this, 15, 77);
        RedstoneOre5.addBox(-1.0F, -5.0F, 5.0F, 5, 5, 5, 0.0F);
        RedstoneOre5.setPos(0.0F, 0.0F, 0.0F);
        
        body.addChild(CoalOre2);
        body.addChild(CoalOre3);
        body.addChild(CoalOre4);
        body.addChild(IronOre1);
        body.addChild(IronOre2);
        body.addChild(IronOre3);
        body.addChild(LapisOre1);
        body.addChild(LapisOre2);
        body.addChild(LapisOre3);
        body.addChild(LapisOre4);
        body.addChild(RedstoneOre1);
        body.addChild(RedstoneOre2);
        body.addChild(RedstoneOre3);
        body.addChild(RedstoneOre4);
        body.addChild(RedstoneOre5);
        head.addChild(CoalOre1);
    }
    
	@Override
	public void setupAnim(ToretoiseEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.entity = entity;
		animFrames = limbSwing;
	}

    @Override
	public void renderToBuffer(PoseStack matrix, VertexConsumer vb, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
        matrix.pushPose();
        int bufferTime = 10;
    	if(entity.angeryTicks > 0 && entity.angeryTicks < ToretoiseEntity.ANGERY_TIME - bufferTime) {
    		double angeryTime = (entity.angeryTicks - ClientTicker.partialTicks) / (ToretoiseEntity.ANGERY_TIME - bufferTime) * Math.PI;
    		angeryTime = Math.sin(angeryTime) * -20;
    		
    		matrix.translate(0, 1., 1);
    		matrix.mulPose(Vector3f.XP.rotationDegrees((float) angeryTime));
    		matrix.translate(0, -1, -1);
    	}
    	
        float animSpeed = 30;
        float animPause = 12;
        
        float actualFrames = animFrames * 10;
        
        float doubleAnimSpeed = animSpeed * 2;
        float animBuff = animSpeed - animPause;
    	
        float scale = 0.02F;
        float bodyTrans = (float) (Math.sin(actualFrames / doubleAnimSpeed * Math.PI) + 1F) * scale;

        float rideMultiplier = 0;
        
        if(entity.rideTime > 0)
        	rideMultiplier = Math.min(30, entity.rideTime - 1 + ClientTicker.partialTicks) / 30.0F;  
        
        bodyTrans *= (1F - rideMultiplier); 
        
        matrix.translate(0, bodyTrans, 0);
        matrix.mulPose(Vector3f.ZP.rotation((bodyTrans - scale) * 0.5F));
        
        body.render(matrix, vb, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
        
        matrix.pushPose();
        matrix.translate(0, bodyTrans, rideMultiplier * 0.3);
        head.xRot = bodyTrans * 2;
        head.render(matrix, vb, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
        matrix.popPose();

        float finalRideMultiplier = rideMultiplier;
        BiConsumer<ModelPart, Float> draw = (renderer, frames) -> {
        	float time = Math.min(animBuff, frames % doubleAnimSpeed);
            float trans = ((float) (Math.sin(time / animBuff * Math.PI) + 1.0) / -2F) * 0.12F + 0.06F;
            
            float rotTime = (frames % doubleAnimSpeed);
            float rot = ((float) Math.sin(rotTime / doubleAnimSpeed * Math.PI) + 1F) * -0.25F;
        	
            trans *= (1F - finalRideMultiplier);
            rot *= (1F - finalRideMultiplier);
            trans += finalRideMultiplier * -0.2;

            matrix.pushPose();
            
            Cube box = renderer.getRandomCube(entity.getRandom());
            double spread = (1F / 16F) * -1.8 * finalRideMultiplier;
            double x = (renderer.x + box.minX);
            double z = (renderer.z + box.minZ);
            x *= (spread / Math.abs(x));
            z *= (spread / Math.abs(z));
            matrix.translate(x, 0, z);
            
            matrix.translate(0, trans, 0);
            float yRot = renderer.yRot;
            renderer.xRot = rot;
            renderer.yRot *= (1F - finalRideMultiplier);
            renderer.render(matrix, vb, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
            renderer.yRot = yRot;
            matrix.popPose();
        };
        
        draw.accept(leftFrontLeg, actualFrames);
        draw.accept(rightFrontLeg, actualFrames + animSpeed);
        draw.accept(leftBackLeg, actualFrames + animSpeed * 0.5F);
        draw.accept(rightBackLeg, actualFrames + animSpeed * 1.5F);
        matrix.popPose();
    }
    
    public void setRotateAngle(ModelPart modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
	
}
