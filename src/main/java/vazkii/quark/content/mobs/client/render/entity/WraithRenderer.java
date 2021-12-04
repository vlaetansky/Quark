package vazkii.quark.content.mobs.client.render.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import vazkii.quark.base.client.handler.ModelHandler;
import vazkii.quark.content.mobs.client.model.WraithModel;
import vazkii.quark.content.mobs.entity.Wraith;

public class WraithRenderer extends MobRenderer<Wraith, WraithModel> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("quark", "textures/model/entity/wraith.png");

	public WraithRenderer(EntityRendererProvider.Context context) {
		super(context, ModelHandler.model(ModelHandler.wraith), 0F);
	}
	
	@Nullable
	@Override
	public ResourceLocation getTextureLocation(@Nonnull Wraith entity) {
		return TEXTURE;
	}
	
}

