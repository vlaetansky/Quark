package vazkii.quark.content.client.render.variant;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.BeeRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Bee;
import vazkii.quark.base.Quark;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class VariantBeeRenderer extends BeeRenderer {

	public static void setOldRenderFactory(EntityRendererProvider<Bee> provider) {
		OLD_RENDER_FACTORY = provider;
	}

	private static EntityRendererProvider<Bee> OLD_RENDER_FACTORY = null;
	private EntityRenderer<? super Bee> OLD_RENDERER = null;

	private static final List<String> VARIANTS = ImmutableList.of(
			"acebee", "agenbee", "arobee", "beefluid", "beesexual",
			"beequeer", "enbee", "gaybee", "interbee", "lesbeean",
			"panbee", "polysexbee", "transbee", "helen");

	public VariantBeeRenderer(EntityRendererProvider.Context context) {
		super(context);

		if(OLD_RENDER_FACTORY != null)
			OLD_RENDERER = OLD_RENDER_FACTORY.create(context);
	}

	@Override
	public boolean shouldRender(@Nonnull Bee bee, @Nonnull Frustum frustum, double viewX, double viewY, double viewZ) {
		if (OLD_RENDERER != null)
			return OLD_RENDERER.shouldRender(bee, frustum, viewX, viewY, viewZ);
		else
			return super.shouldRender(bee, frustum, viewX, viewY, viewZ);
	}

	@Override
	public void render(@Nonnull Bee bee, float yaw, float partialTicks, @Nonnull PoseStack matrix, @Nonnull MultiBufferSource buffer, int light) {
		if (OLD_RENDERER != null)
			OLD_RENDERER.render(bee, yaw, partialTicks, matrix, buffer, light);
		else
			super.render(bee, yaw, partialTicks, matrix, buffer, light);
	}

	@Nonnull
	@Override
	public ResourceLocation getTextureLocation(Bee entity) {
		UUID id = entity.getUUID();
		long most = id.getMostSignificantBits();

		// From https://news.gallup.com/poll/329708/lgbt-identification-rises-latest-estimate.aspx
		final double lgbtChance = 0.056;
		boolean lgbt = VariantAnimalTexturesModule.everyBeeIsLGBT || (new Random(most)).nextDouble() < lgbtChance;

		if(entity.hasCustomName() || lgbt) {
			String custName = entity.hasCustomName() ? entity.getCustomName().getString().trim() : "";
			String name = custName.toLowerCase(Locale.ROOT);

			if(!VARIANTS.contains(name)) {
				if(custName.matches("wire(se|bee)gal"))
					name = "enbee";
				else if(lgbt)
					name = VARIANTS.get(Math.abs((int) (most % (VARIANTS.size() - 1)))); // -1 to not spawn helen bee naturally
			}

			if(VARIANTS.contains(name)) {
				String type = "normal";
				boolean angery = entity.hasStung();
				boolean nectar = entity.hasNectar();

				if(angery)
					type = nectar ? "angry_nectar" : "angry";
				else if(nectar)
					type = "nectar";

				String path = String.format("textures/model/entity/variants/bees/%s/%s.png", name, type);
				return new ResourceLocation(Quark.MOD_ID, path);
			}
		}

		if(OLD_RENDERER != null) {
			return OLD_RENDERER.getTextureLocation(entity);
		}

		return super.getTextureLocation(entity);
	}

}
