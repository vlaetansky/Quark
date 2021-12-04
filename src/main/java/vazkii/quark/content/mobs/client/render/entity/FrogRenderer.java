package vazkii.quark.content.mobs.client.render.entity;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import vazkii.quark.base.client.handler.ModelHandler;
import vazkii.quark.content.mobs.client.model.FrogModel;
import vazkii.quark.content.mobs.entity.Frog;

public class FrogRenderer extends MobRenderer<Frog, FrogModel> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("quark", "textures/model/entity/frog.png");
	private static final ResourceLocation TEXTURE_SWEATER = new ResourceLocation("quark", "textures/model/entity/events/sweater_frog.png");
	private static final ResourceLocation TEXTURE_FUNNY = new ResourceLocation("quark", "textures/model/entity/events/funny_rat_frog.png");
	private static final ResourceLocation TEXTURE_SWEATER_FUNNY = new ResourceLocation("quark", "textures/model/entity/events/sweater_funny_rat_frog.png");
	private static final ResourceLocation TEXTURE_SNAKE = new ResourceLocation("quark", "textures/model/entity/events/snake_block_frog.png");
	private static final ResourceLocation TEXTURE_SWEATER_SNAKE = new ResourceLocation("quark", "textures/model/entity/events/sweater_snake_block_frog.png");
	private static final ResourceLocation TEXTURE_KERMIT = new ResourceLocation("quark", "textures/model/entity/events/kermit_frog.png");
	private static final ResourceLocation TEXTURE_SWEATER_KERMIT = new ResourceLocation("quark", "textures/model/entity/events/sweater_kermit_frog.png");
	private static final ResourceLocation TEXTURE_VOID = new ResourceLocation("quark", "textures/model/entity/events/void_frog.png");
	private static final ResourceLocation TEXTURE_SWEATER_VOID = new ResourceLocation("quark", "textures/model/entity/events/sweater_void_frog.png");

	public FrogRenderer(EntityRendererProvider.Context context) {
		super(context, ModelHandler.model(ModelHandler.frog), 0.2F);
	}

	@Override
	protected void setupRotations(@Nonnull Frog frog, @Nonnull PoseStack matrix, float ageInTicks, float rotationYaw, float partialTicks) {
		super.setupRotations(frog, matrix, ageInTicks, rotationYaw, partialTicks);

		if (frog.isVoid()) {
			matrix.translate(0.0D, frog.getBbHeight(), 0.0D);
			matrix.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
		}
	}

	@Nonnull
	@Override
	public ResourceLocation getTextureLocation(@Nonnull Frog entity) {
		if (entity.isVoid())
			return entity.hasSweater() ? TEXTURE_SWEATER_VOID : TEXTURE_VOID;

		if (entity.hasCustomName()) {
			String name = entity.getCustomName().getString().trim();
			if(name.equalsIgnoreCase("Alex") || name.equalsIgnoreCase("Rat") || name.equalsIgnoreCase("Funny Rat"))
				return entity.hasSweater() ? TEXTURE_SWEATER_FUNNY : TEXTURE_FUNNY;
			if(name.equalsIgnoreCase("Snake") || name.equalsIgnoreCase("SnakeBlock") || name.equalsIgnoreCase("Snake Block"))
				return entity.hasSweater() ? TEXTURE_SWEATER_SNAKE : TEXTURE_SNAKE;
			if(name.equalsIgnoreCase("Kermit"))
				return entity.hasSweater() ? TEXTURE_SWEATER_KERMIT : TEXTURE_KERMIT;
		}
		return entity.hasSweater() ? TEXTURE_SWEATER : TEXTURE;
	}

}
