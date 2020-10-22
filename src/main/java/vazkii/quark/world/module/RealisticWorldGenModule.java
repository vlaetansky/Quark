package vazkii.quark.world.module;

import java.util.Locale;
import java.util.Optional;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.ServerProperties;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.storage.ServerWorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.Module;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.world.client.RealisticGenScreen;
import vazkii.quark.world.gen.RealisticChunkGenerator;

@LoadModule(category = ModuleCategory.WORLD, hasSubscriptions = true, subscribeOn = Dist.DEDICATED_SERVER)
public class RealisticWorldGenModule extends Module {

	public static final ResourceLocation REALISTIC_RES = new ResourceLocation("quark", "realistic");
	public static final RegistryKey<DimensionSettings> REALISTIC_KEY = RegistryKey.getOrCreateKey(Registry.NOISE_SETTINGS_KEY, REALISTIC_RES);


	@Override
	public void construct() {
		Registry.register(Registry.CHUNK_GENERATOR_CODEC, REALISTIC_RES, RealisticChunkGenerator.CODEC);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void constructClient() {
		new RealisticGenScreen();
	}

	private static ChunkGenerator createChunkGenerator(long seed, Registry<Biome> biomes, Registry<DimensionSettings> settings) {
		return new RealisticChunkGenerator(new OverworldBiomeProvider(seed, false, false, biomes), seed, () -> settings.getOrThrow(DimensionSettings.field_242734_c));
	}

	private static DimensionGeneratorSettings createSettings(DynamicRegistries registries, long seed, boolean generateFeatures, boolean generateBonusChest) {
		Registry<Biome> biomes = registries.getRegistry(Registry.BIOME_KEY);
		Registry<DimensionSettings> settings = registries.getRegistry(Registry.NOISE_SETTINGS_KEY);
		Registry<DimensionType> types = registries.getRegistry(Registry.DIMENSION_TYPE_KEY);
		return new DimensionGeneratorSettings(seed, generateFeatures, generateBonusChest, DimensionGeneratorSettings.func_242749_a(types, DimensionType.getDefaultSimpleRegistry(types, biomes, settings, seed), createChunkGenerator(seed, biomes, settings)));
	}

	@SubscribeEvent
	public void onServerStart(FMLServerAboutToStartEvent event) {
		// Check that we're on the dedicated server before checking the world type
		if (event.getServer() instanceof DedicatedServer) {
			DedicatedServer server = (DedicatedServer) event.getServer();
			String levelType = Optional.ofNullable((String)server.getServerProperties().serverProperties.get("level-type")).map(str -> str.toLowerCase(Locale.ROOT)).orElse("default");

			// If the world type is realistic, then replace the worldgen data
			if (levelType.equals("realistic")) {
				if (server.func_240793_aU_() instanceof ServerWorldInfo) {
					ServerWorldInfo worldInfo = (ServerWorldInfo)server.func_240793_aU_();
					worldInfo.generatorSettings = createSettings(server.func_244267_aX(), worldInfo.generatorSettings.getSeed(), worldInfo.generatorSettings.doesGenerateFeatures(), worldInfo.generatorSettings.hasBonusChest());
				}
				ServerProperties properties = server.getServerProperties();
				properties.field_241082_U_ = createSettings(server.func_244267_aX(), properties.field_241082_U_.getSeed(), properties.field_241082_U_.doesGenerateFeatures(), properties.field_241082_U_.hasBonusChest());
			}
		}
	}
}
