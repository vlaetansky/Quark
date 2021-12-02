package vazkii.quark.content.mobs.client.render;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.base.client.handler.ModelHandler;
import vazkii.quark.content.mobs.client.layer.StonelingItemLayer;
import vazkii.quark.content.mobs.client.model.StonelingModel;
import vazkii.quark.content.mobs.entity.StonelingEntity;

@OnlyIn(Dist.CLIENT)
public class StonelingRenderer extends MobRenderer<StonelingEntity, StonelingModel> {

	public StonelingRenderer(EntityRendererProvider.Context context) {
		super(context, ModelHandler.model(ModelHandler.stoneling), 0.3F);
		addLayer(new StonelingItemLayer(this));
	}
	
	@Override
	public ResourceLocation getTextureLocation(@Nonnull StonelingEntity entity) {
		return entity.getVariant().getTexture();
	}

}
