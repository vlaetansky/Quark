package vazkii.quark.content.client.module;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.content.client.render.variant.*;

import java.util.*;
import java.util.function.Supplier;

@LoadModule(category = ModuleCategory.CLIENT, hasSubscriptions = true, subscribeOn = Dist.CLIENT)
public class VariantAnimalTexturesModule extends QuarkModule {

	private static ListMultimap<VariantTextureType, ResourceLocation> textures;
	private static Map<VariantTextureType, ResourceLocation> shinyTextures;

	private static final int COW_COUNT = 4;
	private static final int PIG_COUNT = 3;
	private static final int CHICKEN_COUNT = 6;

	@Config public static boolean enableCow = true;
	@Config public static boolean enablePig = true;
	@Config public static boolean enableChicken = true;
	@Config public static boolean enableShinyRabbit = true;
	@Config public static boolean enableShinyLlama = true;
	@Config public static boolean enableShinyDolphin = true;
	@Config public static boolean enableLGBTBees = true;

	@Config public static boolean everyBeeIsLGBT = false;


	@Config(description = "The chance for an animal to have a special \"Shiny\" skin, like a shiny pokemon. This is 1 in X. Set to 0 to disable.")
	public static int shinyAnimalChance = 2048;

	@Config(description = "If a shiny animal should emit occasional sparkles.")
	public static boolean shinySparkles = true;

	@Override
	public void clientSetup() {
		if(!enabled)
			return;

		textures = Multimaps.newListMultimap(new EnumMap<>(VariantTextureType.class), ArrayList::new);
		shinyTextures = new HashMap<>();

		registerTextures(VariantTextureType.COW, COW_COUNT, new ResourceLocation("textures/entity/cow/cow.png"));
		registerTextures(VariantTextureType.PIG, PIG_COUNT, new ResourceLocation("textures/entity/pig/pig.png"));
		registerTextures(VariantTextureType.CHICKEN, CHICKEN_COUNT, new ResourceLocation("textures/entity/chicken.png"));
		registerShiny(VariantTextureType.RABBIT);
		registerShiny(VariantTextureType.LLAMA);
		registerShiny(VariantTextureType.DOLPHIN);

		if(enableCow)
			EntityRenderers.register(EntityType.COW, VariantCowRenderer::new);
		if(enablePig)
			EntityRenderers.register(EntityType.PIG, VariantPigRenderer::new);
		if(enableChicken)
			EntityRenderers.register(EntityType.CHICKEN, VariantChickenRenderer::new);
		if(enableShinyRabbit)
			EntityRenderers.register(EntityType.RABBIT, VariantRabbitRenderer::new);
		if(enableShinyLlama)
			EntityRenderers.register(EntityType.LLAMA, VariantLlamaRenderer::new);
		if(enableLGBTBees)
			registerAndStackBeeRenderers();
		if(enableShinyDolphin)
			EntityRenderers.register(EntityType.DOLPHIN, VariantDolphinRenderer::new);

	}

	@OnlyIn(Dist.CLIENT)
	public static boolean isShiny(Entity e) {
		EntityType<?> type = e.getType();
		if ((type != EntityType.COW || !enableCow) &&
				(type != EntityType.PIG || !enablePig) &&
				(type != EntityType.CHICKEN || !enableChicken) &&
				(type != EntityType.RABBIT || !enableShinyRabbit) &&
				(type != EntityType.LLAMA || !enableShinyLlama) &&
				(type != EntityType.DOLPHIN || !enableShinyDolphin))
			return false;

		UUID id = e.getUUID();
		long most = id.getMostSignificantBits();
		return shinyAnimalChance > 0 && most % shinyAnimalChance == 0;
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void doShinySparkles(LivingEvent.LivingUpdateEvent event) {
		LivingEntity entity = event.getEntityLiving();
		Level level = entity.getLevel();
		if (level.isClientSide() && level.getGameTime() % 10 == 0) {
			if (isShiny(entity)) {
				double angle = Math.random() * 2 * Math.PI;
				double dist = Math.random() * 0.5 + 0.25;
				double dX = Math.cos(angle) * dist;
				double dY = entity.getDimensions(entity.getPose()).height + (Math.random() - 0.5) * 0.2;
				double dZ = Math.sin(angle) * dist;
				level.addParticle(ParticleTypes.HAPPY_VILLAGER, entity.getX() + dX, entity.getY() + dY, entity.getZ() + dZ, Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void registerAndStackBeeRenderers() {
		VariantBeeRenderer.OLD_BEE_RENDER_FACTORY = (EntityRendererProvider<Bee>) EntityRenderers.PROVIDERS.get(EntityType.BEE);
		EntityRenderers.register(EntityType.BEE, VariantBeeRenderer::new);
	}

	@OnlyIn(Dist.CLIENT)
	public static ResourceLocation getTextureOrShiny(Entity e, VariantTextureType type, boolean enabled) {
		return getTextureOrShiny(e, type, () -> getRandomTexture(e, type, enabled));
	}

	@OnlyIn(Dist.CLIENT)
	public static ResourceLocation getTextureOrShiny(Entity e, VariantTextureType type, Supplier<ResourceLocation> nonShiny) {
		UUID id = e.getUUID();
		long most = id.getMostSignificantBits();
		if(shinyAnimalChance > 0 && (most % shinyAnimalChance) == 0)
			return shinyTextures.get(type);

		return nonShiny.get();
	}

	@OnlyIn(Dist.CLIENT)
	private static ResourceLocation getRandomTexture(Entity e, VariantTextureType type, boolean enabled) {
		List<ResourceLocation> styles = textures.get(type);
		if(!enabled)
			return styles.get(styles.size() - 1);

		UUID id = e.getUUID();
		long most = id.getMostSignificantBits();
		int choice = Math.abs((int) (most % styles.size()));
		return styles.get(choice);
	}

	@OnlyIn(Dist.CLIENT)
	private static void registerTextures(VariantTextureType type, int count, ResourceLocation vanilla) {
		String name = type.name().toLowerCase(Locale.ROOT);
		for(int i = 1; i < count + 1; i++)
			textures.put(type, new ResourceLocation(Quark.MOD_ID, String.format("textures/model/entity/variants/%s%d.png", name, i)));

		if(vanilla != null)
			textures.put(type, vanilla);
		registerShiny(type);
	}

	private static void registerShiny(VariantTextureType type) {
		shinyTextures.put(type, new ResourceLocation(Quark.MOD_ID, String.format("textures/model/entity/variants/%s_shiny.png", type.name().toLowerCase(Locale.ROOT))));
	}

	public enum VariantTextureType {
		COW, PIG, CHICKEN, LLAMA, RABBIT, DOLPHIN
	}

}
