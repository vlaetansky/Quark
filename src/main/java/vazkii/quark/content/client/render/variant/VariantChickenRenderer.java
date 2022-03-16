package vazkii.quark.content.client.render.variant;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.ChickenRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Chicken;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule.VariantTextureType;

import javax.annotation.Nonnull;

public class VariantChickenRenderer extends ChickenRenderer {

	public static void setOldRenderFactory(EntityRendererProvider<Chicken> provider) {
		OLD_RENDER_FACTORY = provider;
	}

	private static EntityRendererProvider<Chicken> OLD_RENDER_FACTORY = null;
	private EntityRenderer<? super Chicken> OLD_RENDERER = null;

	public VariantChickenRenderer(EntityRendererProvider.Context context) {
		super(context);

		if(OLD_RENDER_FACTORY != null)
			OLD_RENDERER = OLD_RENDER_FACTORY.create(context);
	}

	@Override
	public boolean shouldRender(@Nonnull Chicken chicken, @Nonnull Frustum frustum, double viewX, double viewY, double viewZ) {
		if (OLD_RENDERER != null)
			return OLD_RENDERER.shouldRender(chicken, frustum, viewX, viewY, viewZ);
		else
			return super.shouldRender(chicken, frustum, viewX, viewY, viewZ);
	}

	@Override
	public void render(@Nonnull Chicken chicken, float yaw, float partialTicks, @Nonnull PoseStack matrix, @Nonnull MultiBufferSource buffer, int light) {
		if (OLD_RENDERER != null)
			OLD_RENDERER.render(chicken, yaw, partialTicks, matrix, buffer, light);
		else
			super.render(chicken, yaw, partialTicks, matrix, buffer, light);
	}

	@Nonnull
	@Override
	public ResourceLocation getTextureLocation(@Nonnull Chicken entity) {
		return VariantAnimalTexturesModule.getTextureOrShiny(entity, VariantTextureType.CHICKEN, VariantAnimalTexturesModule.enableChicken);
	}

}
