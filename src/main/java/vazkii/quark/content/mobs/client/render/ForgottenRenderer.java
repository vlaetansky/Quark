package vazkii.quark.content.mobs.client.render;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.resources.ResourceLocation;
import vazkii.quark.base.Quark;
import vazkii.quark.content.mobs.client.layer.forgotten.ForgottenClothingLayer;
import vazkii.quark.content.mobs.client.layer.forgotten.ForgottenEyesLayer;
import vazkii.quark.content.mobs.client.layer.forgotten.ForgottenSheathedItemLayer;

public class ForgottenRenderer extends SkeletonRenderer {

	private static final ResourceLocation TEXTURE = new ResourceLocation(Quark.MOD_ID, "textures/model/entity/forgotten/main.png");

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ForgottenRenderer(EntityRenderDispatcher renderManagerIn) {
		super(renderManagerIn);
		addLayer(new ForgottenClothingLayer<>(this));
		addLayer(new ForgottenEyesLayer(this));
		addLayer(new ForgottenSheathedItemLayer(this));
	}

	@Override
	public ResourceLocation getTextureLocation(AbstractSkeleton entity) {
		return TEXTURE;
	}

	@Override
	protected void scale(AbstractSkeleton entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
		matrixStackIn.scale(1.2F, 1.2F, 1.2F);
	}

}
