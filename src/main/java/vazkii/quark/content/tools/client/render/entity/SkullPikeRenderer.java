package vazkii.quark.content.tools.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import vazkii.quark.content.tools.entity.SkullPike;

import javax.annotation.Nonnull;

public class SkullPikeRenderer extends EntityRenderer<SkullPike> {

	public SkullPikeRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(@Nonnull SkullPike p_225623_1_, float p_225623_2_, float p_225623_3_, @Nonnull PoseStack p_225623_4_, @Nonnull MultiBufferSource p_225623_5_, int p_225623_6_) {
	}

	@Nonnull
	@Override
	public ResourceLocation getTextureLocation(@Nonnull SkullPike arg0) {
		return null;
	}

}
