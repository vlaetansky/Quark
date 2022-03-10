package vazkii.quark.content.world.module;

import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.QuarkSounds;
import vazkii.quark.base.handler.UndergroundBiomeHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.mobs.module.StonelingsModule;
import vazkii.quark.content.world.block.GlowLichenGrowthBlock;
import vazkii.quark.content.world.block.GlowShroomBlock;
import vazkii.quark.content.world.block.GlowShroomRingBlock;
import vazkii.quark.content.world.block.HugeGlowShroomBlock;
import vazkii.quark.content.world.feature.GlowExtrasFeature;
import vazkii.quark.content.world.feature.GlowShroomsFeature;

@LoadModule(category = ModuleCategory.WORLD)
public class GlimmeringWealdModule extends QuarkModule {

	private static final Climate.Parameter FULL_RANGE = Climate.Parameter.span(-1.0F, 1.0F);
	public static final ResourceLocation BIOME_NAME = new ResourceLocation(Quark.MOD_ID, "glimmering_weald");

	public static final Holder<PlacedFeature> ORE_LAPIS_EXTRA = PlacementUtils.register("ore_lapis_glimmering_weald", OreFeatures.ORE_LAPIS, OrePlacements.commonOrePlacement(12, HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(0))));
	public static Holder<PlacedFeature> placed_glow_shrooms;
	public static Holder<PlacedFeature> placed_glow_extras;

	public static Block glow_shroom;
	public static Block glow_lichen_growth;
	public static Block glow_shroom_block;
	public static Block glow_shroom_stem;
	public static Block glow_shroom_ring;
	
	public static TagKey<Item> glowShroomFeedablesTag;

	@Override
	public void register() {
		glow_shroom = new GlowShroomBlock(this);
		glow_lichen_growth = new GlowLichenGrowthBlock(this);
		glow_shroom_block = new HugeGlowShroomBlock("glow_shroom_block", this, true);
		glow_shroom_stem = new HugeGlowShroomBlock("glow_shroom_stem", this, false);
		glow_shroom_ring = new GlowShroomRingBlock(this);
		
		makeFeatures();
		
		RegistryHelper.register(makeBiome());
		UndergroundBiomeHandler.addUndergroundBiome(this, Climate.parameters(FULL_RANGE, FULL_RANGE, FULL_RANGE, FULL_RANGE, Climate.Parameter.span(1.55F, 2F), FULL_RANGE, 0F), BIOME_NAME);
	}

	@Override
	public void setup() {
		glowShroomFeedablesTag = ItemTags.create(new ResourceLocation(Quark.MOD_ID, "glow_shroom_feedables"));
	}
	
	private static void makeFeatures() {
		placed_glow_shrooms = place("glow_shrooms", new GlowShroomsFeature(), GlowShroomsFeature.placed());
		placed_glow_extras = place("glow_extras", new GlowExtrasFeature(), GlowExtrasFeature.placed());
	}

	private static Holder<PlacedFeature> place(String featureName, Feature<NoneFeatureConfiguration> feature, List<PlacementModifier> placer) {
		String name = Quark.MOD_ID + ":" + featureName;
		feature.setRegistryName(name);

		RegistryHelper.register(feature);
		Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> configured = FeatureUtils.register(name, feature, NoneFeatureConfiguration.NONE);
		return PlacementUtils.register(name, configured, placer);
	}

	private static Biome makeBiome() {
		MobSpawnSettings.Builder mobs = new MobSpawnSettings.Builder();
		BiomeDefaultFeatures.commonSpawns(mobs);
		
		if(ModuleLoader.INSTANCE.isModuleEnabled(StonelingsModule.class))
			  mobs.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(StonelingsModule.stonelingType, 200, 1, 4));
		mobs.addSpawn(MobCategory.UNDERGROUND_WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.GLOW_SQUID, 20, 4, 6));

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
		
		settings.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ORE_LAPIS_EXTRA);

		Music music = Musics.createGameMusic(QuarkSounds.MUSIC_GLIMMERING_WEALD);
		Biome biome = OverworldBiomes.biome(Biome.Precipitation.RAIN, Biome.BiomeCategory.UNDERGROUND, 0.8F, 0.4F, mobs, settings, music);
		biome.setRegistryName(BIOME_NAME);

		return biome;
	}

}
