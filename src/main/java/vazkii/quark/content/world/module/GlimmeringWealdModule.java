package vazkii.quark.content.world.module;

import java.util.function.Consumer;
import java.util.function.Function;

import com.mojang.datafixers.util.Pair;

import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.world.block.GlowLichenGrowthBlock;
import vazkii.quark.content.world.block.GlowShroomBlock;
import vazkii.quark.content.world.block.GlowShroomRingBlock;
import vazkii.quark.content.world.block.HugeGlowShroomBlock;
import vazkii.quark.content.world.feature.GlowExtrasFeature;
import vazkii.quark.content.world.feature.GlowShroomsFeature;

@LoadModule(category = ModuleCategory.WORLD)
public class GlimmeringWealdModule extends QuarkModule {

	private static final Climate.Parameter FULL_RANGE = Climate.Parameter.span(-1.0F, 1.0F);
	private static final String BIOME_NAME = "glimmering_weald";

	public static PlacedFeature placed_glow_shrooms;
	public static PlacedFeature placed_glow_extras;

	public static ResourceKey<Biome> glimmering_weald;

	public static Block glow_shroom;
	public static Block glow_lichen_growth;
	public static Block glow_shroom_block;
	public static Block glow_shroom_stem;
	public static Block glow_shroom_ring;

	@Override
	public void construct() {
		glow_shroom = new GlowShroomBlock(this);
		glow_lichen_growth = new GlowLichenGrowthBlock(this);
		glow_shroom_block = new HugeGlowShroomBlock("glow_shroom_block", this, true);
		glow_shroom_stem = new HugeGlowShroomBlock("glow_shroom_stem", this, false);
		glow_shroom_ring = new GlowShroomRingBlock(this);
		
		makeFeatures();
		RegistryHelper.register(makeBiome());
	}

	private static void makeFeatures() {
		placed_glow_shrooms = place("glow_shrooms", new GlowShroomsFeature(), GlowShroomsFeature::placed);
		placed_glow_extras = place("glow_extras", new GlowExtrasFeature(), GlowExtrasFeature::placed);
	}

	private static PlacedFeature place(String featureName, Feature<NoneFeatureConfiguration> feature, Function<ConfiguredFeature<NoneFeatureConfiguration, ?>, PlacedFeature> placer) {
		String name = Quark.MOD_ID + ":" + featureName;
		feature.setRegistryName(name);

		RegistryHelper.register(feature);
		ConfiguredFeature<NoneFeatureConfiguration, ?> configured = FeatureUtils.register(name, feature.configured(NoneFeatureConfiguration.NONE));
		return PlacementUtils.register(name, placer.apply(configured));
	}

	private static Biome makeBiome() {
		MobSpawnSettings.Builder mobs = new MobSpawnSettings.Builder();
		BiomeDefaultFeatures.commonSpawns(mobs);

		BiomeGenerationSettings.Builder settings = new BiomeGenerationSettings.Builder();
		OverworldBiomes.globalOverworldGeneration(settings);
		BiomeDefaultFeatures.addPlainGrass(settings);
		BiomeDefaultFeatures.addDefaultOres(settings, true);
		BiomeDefaultFeatures.addDefaultSoftDisks(settings);
		BiomeDefaultFeatures.addPlainVegetation(settings);
		BiomeDefaultFeatures.addDefaultMushrooms(settings);
		BiomeDefaultFeatures.addDefaultExtraVegetation(settings);

		settings.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, placed_glow_shrooms);
		settings.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, placed_glow_extras);

		Music music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DRIPSTONE_CAVES);
		Biome biome = OverworldBiomes.biome(Biome.Precipitation.RAIN, Biome.BiomeCategory.UNDERGROUND, 0.8F, 0.4F, mobs, settings, music);
		biome.setRegistryName(new ResourceLocation(Quark.MOD_ID, BIOME_NAME));

		return biome;
	}

	public static void addUndergroundBiomes(OverworldBiomeBuilder builder, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> consumer) {
		if(!ModuleLoader.INSTANCE.isModuleEnabled(GlimmeringWealdModule.class))
			return;

		if(glimmering_weald == null)
			glimmering_weald = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(Quark.MOD_ID, BIOME_NAME));

		addBiome(consumer, Climate.Parameter.span(1.55F, 2.05F), glimmering_weald);
	}

	private static void addBiome(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> consumer, Climate.Parameter depth, ResourceKey<Biome> biome) {
		consumer.accept(Pair.of(Climate.parameters(FULL_RANGE, FULL_RANGE, FULL_RANGE, FULL_RANGE, depth, FULL_RANGE, 0F), biome));
	}

}
