package vazkii.quark.world.module;

import java.util.Locale;
import java.util.Optional;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.ServerProperties;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
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

	private static DimensionGeneratorSettings createSettings(long seed, boolean generateFeatures, boolean generateBonusChest) {
		return null; // TODO supercoder pls
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
					worldInfo.generatorSettings = createSettings(worldInfo.generatorSettings.getSeed(), worldInfo.generatorSettings.doesGenerateFeatures(), worldInfo.generatorSettings.hasBonusChest());
				}
				ServerProperties properties = server.getServerProperties();
				properties.field_241082_U_ = createSettings(properties.field_241082_U_.getSeed(), properties.field_241082_U_.doesGenerateFeatures(), properties.field_241082_U_.hasBonusChest());
			}
		}
	}
}
