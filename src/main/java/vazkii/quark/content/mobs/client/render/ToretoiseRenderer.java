package vazkii.quark.content.mobs.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import vazkii.quark.base.Quark;
import vazkii.quark.base.client.handler.ModelHandler;
import vazkii.quark.content.mobs.client.layer.ToretoiseOreLayer;
import vazkii.quark.content.mobs.client.model.ToretoiseModel;
import vazkii.quark.content.mobs.entity.ToretoiseEntity;
import vazkii.quark.content.mobs.module.ToretoiseModule;

public class ToretoiseRenderer extends MobRenderer<ToretoiseEntity, ToretoiseModel>{

	private static final ResourceLocation BASE_TEXTURE = new ResourceLocation(Quark.MOD_ID, "textures/model/entity/toretoise/base.png");
	
	public ToretoiseRenderer(EntityRendererProvider.Context context) {
		super(context, ModelHandler.model(ToretoiseModule.layer), 1F);
		addLayer(new ToretoiseOreLayer(this));
	}

	@Override
	public ResourceLocation getTextureLocation(ToretoiseEntity entity) {
		return BASE_TEXTURE;
	}

}
