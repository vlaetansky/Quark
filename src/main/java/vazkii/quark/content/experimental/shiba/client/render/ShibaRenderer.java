package vazkii.quark.content.experimental.shiba.client.render;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import vazkii.quark.base.Quark;
import vazkii.quark.content.experimental.shiba.client.model.ShibaModel;
import vazkii.quark.content.experimental.shiba.entity.ShibaEntity;

public class ShibaRenderer extends MobRenderer<ShibaEntity, ShibaModel> {

	private static final ResourceLocation SHIBA_BASE = new ResourceLocation(Quark.MOD_ID, "textures/model/entity/shiba/shiba.png");
	
	public ShibaRenderer(EntityRendererManager render) {
		super(render, new ShibaModel(), 0.5F);
	}

	@Override
	public ResourceLocation getEntityTexture(ShibaEntity entity) {
		return SHIBA_BASE;
	}
	
}
