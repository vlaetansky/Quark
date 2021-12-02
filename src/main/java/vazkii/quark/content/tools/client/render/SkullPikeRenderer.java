package vazkii.quark.content.tools.client.render;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import vazkii.quark.content.tools.entity.SkullPikeEntity;

public class SkullPikeRenderer extends EntityRenderer<SkullPikeEntity> {

	public SkullPikeRenderer(EntityRendererProvider.Context context) {
		super(context);
	}
	
	@Override
	public void render(SkullPikeEntity p_225623_1_, float p_225623_2_, float p_225623_3_, PoseStack p_225623_4_, MultiBufferSource p_225623_5_, int p_225623_6_) {
	}

	@Override
	public ResourceLocation getTextureLocation(SkullPikeEntity arg0) {
		return null;
	}

}
