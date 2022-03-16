package vazkii.quark.content.client.render.variant;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LlamaRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Llama;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule.VariantTextureType;

import javax.annotation.Nonnull;

public class VariantLlamaRenderer extends LlamaRenderer {

	public static void setOldRenderFactory(EntityRendererProvider<Llama> provider) {
		OLD_RENDER_FACTORY = provider;
	}

	private static EntityRendererProvider<Llama> OLD_RENDER_FACTORY = null;
	private EntityRenderer<? super Llama> OLD_RENDERER = null;

	public VariantLlamaRenderer(EntityRendererProvider.Context context) {
		super(context, ModelLayers.LLAMA);

		if(OLD_RENDER_FACTORY != null)
			OLD_RENDERER = OLD_RENDER_FACTORY.create(context);
	}

	@Override
	public boolean shouldRender(@Nonnull Llama llama, @Nonnull Frustum frustum, double viewX, double viewY, double viewZ) {
		if (OLD_RENDERER != null)
			return OLD_RENDERER.shouldRender(llama, frustum, viewX, viewY, viewZ);
		else
			return super.shouldRender(llama, frustum, viewX, viewY, viewZ);
	}

	@Override
	public void render(@Nonnull Llama llama, float yaw, float partialTicks, @Nonnull PoseStack matrix, @Nonnull MultiBufferSource buffer, int light) {
		if (OLD_RENDERER != null)
			OLD_RENDERER.render(llama, yaw, partialTicks, matrix, buffer, light);
		else
			super.render(llama, yaw, partialTicks, matrix, buffer, light);
	}

	@Nonnull
	@Override
	public ResourceLocation getTextureLocation(@Nonnull Llama entity) {
		return VariantAnimalTexturesModule.getTextureOrShiny(entity, VariantTextureType.LLAMA,
				() -> OLD_RENDERER != null ? OLD_RENDERER.getTextureLocation(entity) : super.getTextureLocation(entity));
	}

}
