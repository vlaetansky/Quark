package vazkii.quark.addons.oddysey.biome;

import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeAmbience;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.biome.MoodSoundAmbience;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidWithNoiseConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import vazkii.quark.addons.oddysey.feature.BlackstoneSpikeFeature;
import vazkii.quark.addons.oddysey.feature.SpiralPillarFeature;
import vazkii.quark.base.Quark;
import vazkii.quark.base.util.BiomeHelper;

public class BlackDesertBiome {
	
	public static final String ID = Quark.MOD_ID + ":black_desert";
	
	public static ConfiguredFeature<?, ?> spiral_pillar = BiomeHelper.registerFeature(Quark.MOD_ID + ":spiral_pillar", 
			SpiralPillarFeature::new,
			Placement.field_242906_k.configure(NoPlacementConfig.field_236556_b_), // heightmap_world_surface
			Placement.field_242903_g.configure(NoPlacementConfig.field_236556_b_), // square
			Placement.field_242898_b.configure(new ChanceConfig(3))); // chance
	
	public static ConfiguredFeature<?, ?> blackstone_spike = BiomeHelper.registerFeature(Quark.MOD_ID + ":blackstone_spike", 
			BlackstoneSpikeFeature::new,
			Placement.field_242906_k.configure(NoPlacementConfig.field_236556_b_), // heightmap_world_surface
			Placement.field_242903_g.configure(NoPlacementConfig.field_236556_b_), // square
			Placement.field_242901_e.configure(new TopSolidWithNoiseConfig(5, 0.2F, 1F))); // count_noise_biased
	
	public static Biome biome() {
		MobSpawnInfo.Builder mobs = new MobSpawnInfo.Builder();
		DefaultBiomeFeatures.withDesertMobs(mobs);

		BiomeGenerationSettings.Builder builder = new BiomeGenerationSettings.Builder()
				.withSurfaceBuilder(BiomeHelper.getConfiguredSurfaceBuilder(ID, SurfaceBuilder.DEFAULT, 
						new SurfaceBuilderConfig(Blocks.BLACKSTONE.getDefaultState(), Blocks.BLACKSTONE.getDefaultState(), Blocks.GRAVEL.getDefaultState())));

		BiomeHelper.withBasicBiomeStuff(builder);
		DefaultBiomeFeatures.withNormalMushroomGeneration(builder);
		
		builder.withFeature(Decoration.SURFACE_STRUCTURES, spiral_pillar);
		builder.withFeature(Decoration.SURFACE_STRUCTURES, blackstone_spike);

		return (new Biome.Builder())
				.precipitation(Biome.RainType.NONE)
				.category(Biome.Category.DESERT)
				.depth(0.125F)
				.scale(0.05F)
				.temperature(2.0F)
				.downfall(0.0F)
				.setEffects((new BiomeAmbience.Builder())
						.withGrassColor(0x3a3a3a)
						.setWaterColor(4159204)
						.setWaterFogColor(329011)
						.setFogColor(12638463)
						.withSkyColor(BiomeHelper.getSkyColorWithTemperatureModifier(2.0F))
						.setMoodSound(MoodSoundAmbience.DEFAULT_CAVE)
						.build())
				.withMobSpawnSettings(mobs.copy())
				.withGenerationSettings(builder.build())
				.build();
	}

}
