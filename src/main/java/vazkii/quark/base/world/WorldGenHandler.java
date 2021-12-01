package vazkii.quark.base.world;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.GeneralConfig;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.world.generator.IGenerator;

@EventBusSubscriber(modid = Quark.MOD_ID)
public class WorldGenHandler {

	private static Map<GenerationStep.Decoration, Feature<NoneFeatureConfiguration>> defersBaseFeature = new HashMap<>();
	private static Map<GenerationStep.Decoration, PlacedFeature> defers = new HashMap<>();
	private static Map<GenerationStep.Decoration, SortedSet<WeightedGenerator>> generators = new HashMap<>();

	private static Map<GenerationStep.Decoration, Map<PlacedFeature, BooleanSupplier>> featureConditionalizers = new HashMap<>();
	private static Map<PlacedFeature, ConditionalPlacedFeature> featureReplacers = new HashMap<>();

	public static PlacementModifierType<ChunkCornerPlacement> CHUNK_CORNER_PLACEMENT_TYPE = () -> ChunkCornerPlacement.CODEC;
	public static ChunkCornerPlacement CHUNK_CORNER_PLACEMENT = new ChunkCornerPlacement();

	public static void register() {
		registerFeatures();
	}

	private static void registerFeatures() {
		for(GenerationStep.Decoration stage : GenerationStep.Decoration.values()) {
			Feature<NoneFeatureConfiguration> deferredFeature = new DeferedFeature(stage);

			// Always do .toLowerCase(Locale.ENGLISH) with that locale. If you leave it off, computers in
			// countries like Turkey will use a special character instead of i and well, crash the ResourceLocation.
			deferredFeature.setRegistryName(Quark.MOD_ID, "deferred_feature_" + stage.name().toLowerCase(Locale.ENGLISH));
			RegistryHelper.register(deferredFeature);
			defersBaseFeature.put(stage, deferredFeature);
		}
	}

	public static void loadComplete(FMLLoadCompleteEvent event) {
		event.enqueueWork(() -> {
			for(GenerationStep.Decoration stage : GenerationStep.Decoration.values()) {
				ConfiguredFeature<?, ?> feature = defersBaseFeature.get(stage).configured(FeatureConfiguration.NONE);

				ResourceLocation resloc = new ResourceLocation(Quark.MOD_ID, "deferred_feature_" + stage.name().toLowerCase(Locale.ROOT));
				Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, resloc, feature);
				
				PlacedFeature placed = feature.placed(CHUNK_CORNER_PLACEMENT);
				Registry.register(BuiltinRegistries.PLACED_FEATURE, resloc, placed);
				
				defers.put(stage, placed);
			}
			
			Registry.register(Registry.PLACEMENT_MODIFIERS, new ResourceLocation(Quark.MOD_ID, "chunk_corner"), CHUNK_CORNER_PLACEMENT_TYPE);

			setupConditionalizers();
		});
	}

	public static void setupConditionalizers() {
		// Store CFs we make so we can register after the main loop and prevent a CME error.
		for(GenerationStep.Decoration step : featureConditionalizers.keySet()) {
			Map<PlacedFeature, BooleanSupplier> conditions = featureConditionalizers.get(step);
			
			for(PlacedFeature feature : conditions.keySet()) {
				ConditionalPlacedFeature conditioned = new ConditionalPlacedFeature(feature, conditions.get(feature));
				featureReplacers.put(feature, conditioned);
			}
		}
		
		
//		Set<ConfiguredFeature<?, ?>> quarkFeaturesToRegister = new HashSet<>();
//
//		for(Map.Entry<ResourceKey<ConfiguredFeature<?,?>>, ConfiguredFeature<?, ?>> cfEntry : BuiltinRegistries.CONFIGURED_FEATURE.entrySet()){
//			ConfiguredFeature<?,?> configuredFeature = cfEntry.getValue();
//
//			Feature<?> feature = configuredFeature.feature;
//			FeatureConfiguration config = configuredFeature.config;
//
//			// Get the base feature of the CF. Will not get nested CFs such as trees in Feature.RANDOM_SELECTOR.
//			while(config instanceof DecoratedFeatureConfiguration) {
//				DecoratedFeatureConfiguration dconfig = (DecoratedFeatureConfiguration) config;
//				feature = dconfig.feature.get().feature;
//				config = dconfig.feature.get().config;
//			}
//
//			for(Map.Entry<GenerationStep.Decoration, List<Pair<BiPredicate<Feature<? extends FeatureConfiguration>, FeatureConfiguration>, BooleanSupplier>>> conditionalizerEntry : featureConditionalizers.entrySet())
//				for(Pair<BiPredicate<Feature<? extends FeatureConfiguration>, FeatureConfiguration>, BooleanSupplier> pair : conditionalizerEntry.getValue())
//					if(pair.getLeft().test(feature, config)) {
//						ConditionalPlacedFeature<?, ?> conditionalConfiguredFeature = new ConditionalPlacedFeature<>(configuredFeature, pair.getRight());
//						quarkFeaturesToRegister.add(conditionalConfiguredFeature);
//
//						// Turn into JSON so we can know what configuredfeature needs to be replaced in the biome.
//						// The CF in the biome in biomeLoadEvent is NOT the same object as in the WorldGenRegistries.
//						// That's why we need to compare using JSON so we know we replaced the right CF.
//						Optional<JsonElement> tempOptional = ConfiguredFeature.DIRECT_CODEC.encode(configuredFeature, JsonOps.INSTANCE, JsonOps.INSTANCE.empty()).get().left();
//						String cfJSON = tempOptional.isPresent() ? tempOptional.get().toString() : "";
//						featureReplacers.put(cfJSON, Pair.of(conditionalizerEntry.getKey(), conditionalConfiguredFeature));
//					}
//		}
//
//		// Done here to prevent a ConcurrentModificationException if we tried registering within the loop above.
//		int cfIdOffset = 1;
//		for(ConfiguredFeature<?,?> cfToRegister : quarkFeaturesToRegister){
//			// Done with cfIdOffset on the object itself so each newly made cf is unique and doesn't registry replace each other.
//			Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new ResourceLocation(Quark.MOD_ID, "conditional_configured_feature_"+cfIdOffset), cfToRegister);
//			cfIdOffset++;
//		}
	}

	@SubscribeEvent
	public static void onBiomesLoaded(BiomeLoadingEvent ev) {
		BiomeGenerationSettingsBuilder settings = ev.getGeneration();

		for(GenerationStep.Decoration stage : GenerationStep.Decoration.values()) {
			List<Supplier<PlacedFeature>> features = settings.getFeatures(stage);
			features.add(() -> defers.get(stage));

			// Only check generation stages that we are gonna replace stuff in
			if(featureConditionalizers.containsKey(stage)) {
				for(int i = 0; i < features.size(); i++) {
					PlacedFeature placed = features.get(i).get();
					if(featureReplacers.containsKey(placed))
						features.set(i, () -> featureReplacers.get(placed));
				}
				
//				for(int i = 0; i < features.size(); i++) {
//					// Turn current CF to JSON so we can check if we should replace it
//					PlacedFeature placedFeature = features.get(i).get();
//					Optional<JsonElement> tempOptional = ConfiguredFeature.DIRECT_CODEC.encode(placedFeature, JsonOps.INSTANCE, JsonOps.INSTANCE.empty()).get().left();
//					String cfTargetJSON = tempOptional.isPresent() ? tempOptional.get().toString(): "";
//
//					// If the CF does have a replacement entry and the generation stage matches, replace it.
//					if(featureReplacers.containsKey(cfTargetJSON) && featureReplacers.get(cfTargetJSON).getLeft() == stage) {
//						features.set(i, () -> featureReplacers.get(cfTargetJSON).getRight());
//					}
//				}
			}
		}
	}

	public static void addGenerator(QuarkModule module, IGenerator generator, GenerationStep.Decoration stage, int weight) {
		WeightedGenerator weighted = new WeightedGenerator(module, generator, weight);
		if(!generators.containsKey(stage))
			generators.put(stage, new TreeSet<>());

		generators.get(stage).add(weighted);
	}

	public static void conditionalizeFeature(GenerationStep.Decoration stage, PlacedFeature feature, BooleanSupplier condition) {
		if(!featureConditionalizers.containsKey(stage))
			featureConditionalizers.put(stage, new HashMap<>());

		Map<PlacedFeature, BooleanSupplier> map = featureConditionalizers.get(stage);
		map.put(feature, condition);
	}

	public static void generateChunk(FeaturePlaceContext<NoneFeatureConfiguration> context, GenerationStep.Decoration stage) {
		WorldGenLevel level = context.level();
		if(!(level instanceof WorldGenRegion))
			return;

		ChunkGenerator generator = context.chunkGenerator();
		BlockPos pos = context.origin();
		WorldGenRegion region = (WorldGenRegion) level;
		WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(region.getSeed()));
		ChunkPos center = region.getCenter();
		long seed = random.setDecorationSeed(region.getSeed(), center.x * 16, center.z * 16);
		int stageNum = stage.ordinal() * 10000;

		if(generators.containsKey(stage)) {
			SortedSet<WeightedGenerator> set = generators.get(stage);

			for(WeightedGenerator wgen : set) {
				IGenerator gen = wgen.generator;

				if(wgen.module.enabled && gen.canGenerate(region)) {
					if(GeneralConfig.enableWorldgenWatchdog) {
						final int finalStageNum = stageNum;
						stageNum = watchdogRun(gen, () -> gen.generate(finalStageNum, seed, stage, region, generator, random, pos), 1, TimeUnit.MINUTES);
					} else stageNum = gen.generate(stageNum, seed, stage, region, generator, random, pos);
				}
			}
		}
	}

	private static int watchdogRun(IGenerator gen, Callable<Integer> run, int time, TimeUnit unit) {
		ExecutorService exec = Executors.newSingleThreadExecutor();
		Future<Integer> future = exec.submit(run);
		exec.shutdown();

		try {
			return future.get(time, unit);
		} catch(Exception e) {
			throw new RuntimeException("Error generating " + gen, e);
		} 
	}

}
