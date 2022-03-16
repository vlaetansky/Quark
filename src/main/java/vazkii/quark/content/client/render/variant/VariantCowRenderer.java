package vazkii.quark.content.client.render.variant;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.CowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cow;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule.VariantTextureType;

import javax.annotation.Nonnull;

public class VariantCowRenderer extends CowRenderer {

	public static void setOldRenderFactory(EntityRendererProvider<Cow> provider) {
		OLD_RENDER_FACTORY = provider;
	}

	private static EntityRendererProvider<Cow> OLD_RENDER_FACTORY = null;
	private EntityRenderer<? super Cow> OLD_RENDERER = null;

	public VariantCowRenderer(EntityRendererProvider.Context context) {
		super(context);

		if(OLD_RENDER_FACTORY != null)
			OLD_RENDERER = OLD_RENDER_FACTORY.create(context);
	}

	@Override
	public boolean shouldRender(@Nonnull Cow cow, @Nonnull Frustum frustum, double viewX, double viewY, double viewZ) {
		if (OLD_RENDERER != null)
			return OLD_RENDERER.shouldRender(cow, frustum, viewX, viewY, viewZ);
		else
			return super.shouldRender(cow, frustum, viewX, viewY, viewZ);
	}

	@Override
	public void render(@Nonnull Cow cow, float yaw, float partialTicks, @Nonnull PoseStack matrix, @Nonnull MultiBufferSource buffer, int light) {
		if (OLD_RENDERER != null)
			OLD_RENDERER.render(cow, yaw, partialTicks, matrix, buffer, light);
		else
			super.render(cow, yaw, partialTicks, matrix, buffer, light);
	}

	@Nonnull
	@Override
	public ResourceLocation getTextureLocation(@Nonnull Cow entity) {
		return VariantAnimalTexturesModule.getTextureOrShiny(entity, VariantTextureType.COW, VariantAnimalTexturesModule.enableCow);
	}

}
