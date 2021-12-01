package vazkii.quark.content.mobs.client.render;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import vazkii.quark.content.mobs.client.model.WraithModel;
import vazkii.quark.content.mobs.entity.WraithEntity;

public class WraithRenderer extends MobRenderer<WraithEntity, WraithModel> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("quark", "textures/model/entity/wraith.png");

	public WraithRenderer(EntityRenderDispatcher render) {
		super(render, new WraithModel(), 0F);
	}

	@Nullable
	@Override
	public ResourceLocation getTextureLocation(@Nonnull WraithEntity entity) {
		return TEXTURE;
	}
	
}

