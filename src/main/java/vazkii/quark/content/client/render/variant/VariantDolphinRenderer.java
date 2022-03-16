package vazkii.quark.content.client.render.variant;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.DolphinRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Dolphin;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule.VariantTextureType;

import javax.annotation.Nonnull;

public class VariantDolphinRenderer extends DolphinRenderer {

	public static void setOldRenderFactory(EntityRendererProvider<Dolphin> provider) {
		OLD_RENDER_FACTORY = provider;
	}

	private static EntityRendererProvider<Dolphin> OLD_RENDER_FACTORY = null;
	private EntityRenderer<? super Dolphin> OLD_RENDERER = null;

	public VariantDolphinRenderer(EntityRendererProvider.Context context) {
		super(context);

		if(OLD_RENDER_FACTORY != null)
			OLD_RENDERER = OLD_RENDER_FACTORY.create(context);
	}

	@Override
	public boolean shouldRender(@Nonnull Dolphin dolphin, @Nonnull Frustum frustum, double viewX, double viewY, double viewZ) {
		if (OLD_RENDERER != null)
			return OLD_RENDERER.shouldRender(dolphin, frustum, viewX, viewY, viewZ);
		else
			return super.shouldRender(dolphin, frustum, viewX, viewY, viewZ);
	}

	@Override
	public void render(@Nonnull Dolphin dolphin, float yaw, float partialTicks, @Nonnull PoseStack matrix, @Nonnull MultiBufferSource buffer, int light) {
		if (OLD_RENDERER != null)
			OLD_RENDERER.render(dolphin, yaw, partialTicks, matrix, buffer, light);
		else
			super.render(dolphin, yaw, partialTicks, matrix, buffer, light);
	}

	@Nonnull
	@Override
	public ResourceLocation getTextureLocation(@Nonnull Dolphin entity) {
		return VariantAnimalTexturesModule.getTextureOrShiny(entity, VariantTextureType.DOLPHIN,
				() -> OLD_RENDERER != null ? OLD_RENDERER.getTextureLocation(entity) : super.getTextureLocation(entity));
	}

}
