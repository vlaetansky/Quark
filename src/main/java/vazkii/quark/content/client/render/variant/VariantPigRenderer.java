package vazkii.quark.content.client.render.variant;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Pig;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule.VariantTextureType;

import javax.annotation.Nonnull;

public class VariantPigRenderer extends PigRenderer {

	public static void setOldRenderFactory(EntityRendererProvider<Pig> provider) {
		OLD_RENDER_FACTORY = provider;
	}

	private static EntityRendererProvider<Pig> OLD_RENDER_FACTORY = null;
	private EntityRenderer<? super Pig> OLD_RENDERER = null;

	public VariantPigRenderer(EntityRendererProvider.Context context) {
		super(context);

		if(OLD_RENDER_FACTORY != null)
			OLD_RENDERER = OLD_RENDER_FACTORY.create(context);
	}

	@Override
	public boolean shouldRender(@Nonnull Pig pig, @Nonnull Frustum frustum, double viewX, double viewY, double viewZ) {
		if (OLD_RENDERER != null)
			return OLD_RENDERER.shouldRender(pig, frustum, viewX, viewY, viewZ);
		else
			return super.shouldRender(pig, frustum, viewX, viewY, viewZ);
	}

	@Override
	public void render(@Nonnull Pig pig, float yaw, float partialTicks, @Nonnull PoseStack matrix, @Nonnull MultiBufferSource buffer, int light) {
		if (OLD_RENDERER != null)
			OLD_RENDERER.render(pig, yaw, partialTicks, matrix, buffer, light);
		else
			super.render(pig, yaw, partialTicks, matrix, buffer, light);
	}

	@Nonnull
	@Override
	public ResourceLocation getTextureLocation(@Nonnull Pig entity) {
		return VariantAnimalTexturesModule.getTextureOrShiny(entity, VariantTextureType.PIG, VariantAnimalTexturesModule.enablePig);
	}

}
