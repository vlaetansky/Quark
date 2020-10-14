package vazkii.quark.world.module;

import java.util.Locale;
import java.util.Optional;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.Module;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.world.gen.RealisticChunkGenerator;
import vazkii.quark.world.client.RealisticGenScreen;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.ServerProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.storage.ServerWorldInfo;

@LoadModule(category = ModuleCategory.WORLD, hasSubscriptions = true, subscribeOn = Dist.DEDICATED_SERVER)
public class RealisticWorldGenModule extends Module {
// TODO this bork
//	@Override
//	public void construct() {
//		Registry.register(Registry.field_239690_aB_, new ResourceLocation("quark", "realistic"), RealisticChunkGenerator.CODEC);
//	}
//
//	@Override
//	@OnlyIn(Dist.CLIENT)
//	public void constructClient() {
//		new RealisticGenScreen();
//	}
//
//	private static DimensionGeneratorSettings createSettings(long seed, boolean generateFeatures, boolean generateBonusChest) {
//		return new DimensionGeneratorSettings(seed, generateFeatures, generateBonusChest, 
//				DimensionGeneratorSettings.func_236216_a_(DimensionType.func_236022_a_(seed), 
//						new RealisticChunkGenerator(new OverworldBiomeProvider(seed, false, false), seed, DimensionSettings.Preset.field_236122_b_.func_236137_b_())));
//	}
//
//	@SubscribeEvent
//	public void onServerStart(FMLServerAboutToStartEvent event) {
//		// Check that we're on the dedicated server before checking the world type
//		if (event.getServer() instanceof DedicatedServer) {
//			DedicatedServer server = (DedicatedServer) event.getServer();
//			String levelType = Optional.ofNullable((String)server.getServerProperties().serverProperties.get("level-type")).map(str -> str.toLowerCase(Locale.ROOT)).orElse("default");
//
//			// If the world type is realistic, then replace the worldgen data
//			if (levelType.equals("realistic")) {
//				if (server.func_240793_aU_() instanceof ServerWorldInfo) {
//					ServerWorldInfo worldInfo = (ServerWorldInfo)server.func_240793_aU_();
//					worldInfo.field_237343_c_ = createSettings(worldInfo.field_237343_c_.func_236221_b_(), worldInfo.field_237343_c_.func_236222_c_(), worldInfo.field_237343_c_.func_236223_d_());
//				}
//				ServerProperties properties = server.getServerProperties();
//				properties.field_241082_U_ = createSettings(properties.field_241082_U_.func_236221_b_(), properties.field_241082_U_.func_236222_c_(), properties.field_241082_U_.func_236223_d_());
//			}
//		}
//	}
}
