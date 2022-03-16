package vazkii.quark.content.client.render.variant;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RabbitRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Rabbit;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule.VariantTextureType;

import javax.annotation.Nonnull;

public class VariantRabbitRenderer extends RabbitRenderer {

	public static void setOldRenderFactory(EntityRendererProvider<Rabbit> provider) {
		OLD_RENDER_FACTORY = provider;
	}

	private static EntityRendererProvider<Rabbit> OLD_RENDER_FACTORY = null;
	private EntityRenderer<? super Rabbit> OLD_RENDERER = null;

	public VariantRabbitRenderer(EntityRendererProvider.Context context) {
		super(context);

		if(OLD_RENDER_FACTORY != null)
			OLD_RENDERER = OLD_RENDER_FACTORY.create(context);
	}

	@Override
	public boolean shouldRender(@Nonnull Rabbit rabbit, @Nonnull Frustum frustum, double viewX, double viewY, double viewZ) {
		if (OLD_RENDERER != null)
			return OLD_RENDERER.shouldRender(rabbit, frustum, viewX, viewY, viewZ);
		else
			return super.shouldRender(rabbit, frustum, viewX, viewY, viewZ);
	}

	@Override
	public void render(@Nonnull Rabbit rabbit, float yaw, float partialTicks, @Nonnull PoseStack matrix, @Nonnull MultiBufferSource buffer, int light) {
		if (OLD_RENDERER != null)
			OLD_RENDERER.render(rabbit, yaw, partialTicks, matrix, buffer, light);
		else
			super.render(rabbit, yaw, partialTicks, matrix, buffer, light);
	}

	@Nonnull
	@Override
	public ResourceLocation getTextureLocation(@Nonnull Rabbit entity) {
		return VariantAnimalTexturesModule.getTextureOrShiny(entity, VariantTextureType.RABBIT,
				() -> OLD_RENDERER != null ? OLD_RENDERER.getTextureLocation(entity) : super.getTextureLocation(entity));
	}

}
