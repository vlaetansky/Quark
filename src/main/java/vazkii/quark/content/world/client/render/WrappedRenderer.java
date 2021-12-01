package vazkii.quark.content.world.client.render;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.resources.ResourceLocation;
import vazkii.quark.base.Quark;

public class WrappedRenderer extends ZombieRenderer {

	private static final ResourceLocation TEXTURE = new ResourceLocation(Quark.MOD_ID, "textures/model/entity/wrapped.png");

	public WrappedRenderer(EntityRenderDispatcher renderManagerIn) {
		super(renderManagerIn);
	}
	
	@Override
	public ResourceLocation getTextureLocation(Zombie entity) {
		return TEXTURE;
	}

}
