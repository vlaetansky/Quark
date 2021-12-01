package vazkii.quark.content.mobs.client.render;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import vazkii.quark.base.Quark;
import vazkii.quark.content.mobs.client.layer.ToretoiseOreLayer;
import vazkii.quark.content.mobs.client.model.ToretoiseModel;
import vazkii.quark.content.mobs.entity.ToretoiseEntity;

public class ToretoiseRenderer extends MobRenderer<ToretoiseEntity, ToretoiseModel>{

	private static final ResourceLocation BASE_TEXTURE = new ResourceLocation(Quark.MOD_ID, "textures/model/entity/toretoise/base.png");
	
	public ToretoiseRenderer(EntityRenderDispatcher m) {
		super(m, new ToretoiseModel(), 1F);
		addLayer(new ToretoiseOreLayer(this));
	}

	@Override
	public ResourceLocation getTextureLocation(ToretoiseEntity entity) {
		return BASE_TEXTURE;
	}

}
