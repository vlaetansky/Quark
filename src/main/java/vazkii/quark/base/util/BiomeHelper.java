package vazkii.quark.base.util;

import java.util.function.Function;

import com.mojang.serialization.Codec;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import vazkii.arl.util.RegistryHelper;

public class BiomeHelper {

	public static ConfiguredFeature<?, ?> registerFeature(String id, Function<Codec<NoFeatureConfig>, Feature<NoFeatureConfig>> featureSupplier, ConfiguredPlacement<?>... placements) {
		Feature<NoFeatureConfig> feature = featureSupplier.apply(NoFeatureConfig.field_236558_a_);
		RegistryHelper.register(feature, id);
		
		ConfiguredFeature<?, ?> conf = feature.withConfiguration(NoFeatureConfig.field_236559_b_);
		for(ConfiguredPlacement<?> pl : placements)
			conf = conf.withPlacement(pl);
		
		return register(id, conf);
	}

	public static SurfaceBuilder<SurfaceBuilderConfig> getSurfaceBuilder(String id, Function<Codec<SurfaceBuilderConfig>, SurfaceBuilder<SurfaceBuilderConfig>> constr) {
		return register(id, constr.apply(SurfaceBuilderConfig.field_237203_a_));
	}

	public static ConfiguredSurfaceBuilder<SurfaceBuilderConfig> getConfiguredSurfaceBuilder(String id, SurfaceBuilder<SurfaceBuilderConfig> surfaceBuilder, SurfaceBuilderConfig config) {
		return register(id, surfaceBuilder.func_242929_a(config));
	}

	public static ConfiguredSurfaceBuilder<SurfaceBuilderConfig> getConfiguredSurfaceBuilder(String id, Function<Codec<SurfaceBuilderConfig>, SurfaceBuilder<SurfaceBuilderConfig>> surfaceBuilderConstructor, SurfaceBuilderConfig config) {
		return getConfiguredSurfaceBuilder(id, getSurfaceBuilder(id, surfaceBuilderConstructor), config);
	}

	public static int getSkyColorWithTemperatureModifier(float temperature) {
		float lvt_1_1_ = temperature / 3.0F;
		lvt_1_1_ = MathHelper.clamp(lvt_1_1_, -1.0F, 1.0F);
		return MathHelper.hsvToRGB(0.62222224F - lvt_1_1_ * 0.05F, 0.5F + lvt_1_1_ * 0.1F, 1.0F);
	}

	public static void withBasicBiomeStuff(BiomeGenerationSettings.Builder builder) {
		DefaultBiomeFeatures.withStrongholdAndMineshaft(builder);
		DefaultBiomeFeatures.withCavesAndCanyons(builder);
		DefaultBiomeFeatures.withLavaLakes(builder);
		DefaultBiomeFeatures.withMonsterRoom(builder);
		DefaultBiomeFeatures.withCommonOverworldBlocks(builder);
		DefaultBiomeFeatures.withOverworldOres(builder);
		DefaultBiomeFeatures.withDisks(builder);
	}

	private static <SC extends ISurfaceBuilderConfig> ConfiguredSurfaceBuilder<SC> register(String id, ConfiguredSurfaceBuilder<SC> builder) {
		return WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_SURFACE_BUILDER, id, builder);
	}

	private static <C extends ISurfaceBuilderConfig, F extends SurfaceBuilder<C>> F register(String key, F builderIn) {
		return Registry.register(Registry.SURFACE_BUILDER, key, builderIn);
	}

	private static <C extends IFeatureConfig, F extends Feature<C>> F register(String key, F value) {
		return Registry.register(Registry.FEATURE, key, value);
	}

	private static <FC extends IFeatureConfig> ConfiguredFeature<FC, ?> register(String key, ConfiguredFeature<FC, ?> configuredFeature) {
		return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, key, configuredFeature);
	}

}
