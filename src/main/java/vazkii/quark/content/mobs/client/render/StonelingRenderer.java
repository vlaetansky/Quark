package vazkii.quark.content.mobs.client.render;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.content.mobs.client.layer.StonelingItemLayer;
import vazkii.quark.content.mobs.client.model.StonelingModel;
import vazkii.quark.content.mobs.entity.StonelingEntity;

@OnlyIn(Dist.CLIENT)
public class StonelingRenderer extends MobRenderer<StonelingEntity, StonelingModel> {

	public StonelingRenderer(EntityRenderDispatcher renderManager) {
		super(renderManager, new StonelingModel(), 0.3F);
		addLayer(new StonelingItemLayer(this));
	}
	
	@Override
	public ResourceLocation getTextureLocation(@Nonnull StonelingEntity entity) {
		return entity.getVariant().getTexture();
	}

}
