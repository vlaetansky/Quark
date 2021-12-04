package vazkii.quark.content.mobs.client.render.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import vazkii.quark.base.client.handler.ModelHandler;
import vazkii.quark.content.mobs.client.model.CrabModel;
import vazkii.quark.content.mobs.entity.Crab;

public class CrabRenderer extends MobRenderer<Crab, CrabModel> {

	private static final ResourceLocation[] TEXTURES = new ResourceLocation[] {
			new ResourceLocation("quark", "textures/model/entity/crab/red.png"),
			new ResourceLocation("quark", "textures/model/entity/crab/blue.png"),
			new ResourceLocation("quark", "textures/model/entity/crab/green.png")
	};

	public CrabRenderer(EntityRendererProvider.Context context) {
		super(context, ModelHandler.model(ModelHandler.crab), 0.4F);
	}

	@Nullable
	@Override
	public ResourceLocation getTextureLocation(@Nonnull Crab entity) {
		return TEXTURES[Math.min(TEXTURES.length - 1, entity.getVariant())];
	}
}
