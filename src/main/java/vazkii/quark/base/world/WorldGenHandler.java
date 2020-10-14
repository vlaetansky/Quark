package vazkii.quark.base.world;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.GeneralConfig;
import vazkii.quark.base.module.Module;
import vazkii.quark.base.world.generator.IGenerator;

@EventBusSubscriber(modid = Quark.MOD_ID)
public class WorldGenHandler {

	private static Map<GenerationStage.Decoration, Supplier<ConfiguredFeature<?, ?>>> defers = new HashMap<>();
	private static Map<GenerationStage.Decoration, SortedSet<WeightedGenerator>> generators = new HashMap<>();

	private static Map<GenerationStage.Decoration, List<Pair<BiPredicate<Feature<? extends IFeatureConfig>, IFeatureConfig>, BooleanSupplier>>> featureConditionalizers = new HashMap<>();

	public static void loadComplete() {
		for(GenerationStage.Decoration stage : GenerationStage.Decoration.values()) {
			ConfiguredFeature<?, ?> feature = new DeferedFeature(stage).withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(new ChunkCornerPlacement().configure(NoPlacementConfig.NO_PLACEMENT_CONFIG));
			defers.put(stage, () -> feature);
		}
	}

	@SubscribeEvent
	public static void onBiomesLoaded(BiomeLoadingEvent ev) {
		BiomeGenerationSettingsBuilder settings = ev.getGeneration();

		for(GenerationStage.Decoration stage : GenerationStage.Decoration.values()) {
			List<Supplier<ConfiguredFeature<?, ?>>> features = settings.getFeatures(stage);
			features.add(defers.get(stage));

			if(featureConditionalizers.containsKey(stage)) {
				List<Pair<BiPredicate<Feature<? extends IFeatureConfig>, IFeatureConfig>, BooleanSupplier>> list = featureConditionalizers.get(stage);

				for(int i = 0; i < features.size(); i++) {
					ConfiguredFeature<?, ?> configuredFeature = features.get(i).get();

					if(!(configuredFeature instanceof ConditionalConfiguredFeature)) {
						Feature<?> feature = configuredFeature.feature;
						IFeatureConfig config = configuredFeature.config;

						while(config instanceof DecoratedFeatureConfig) {
							DecoratedFeatureConfig dconfig = (DecoratedFeatureConfig) config;
							feature = dconfig.feature.get().feature;
							config = dconfig.feature.get().config;
						}
						
						System.out.println(feature.getRegistryName());
						
						for(Pair<BiPredicate<Feature<? extends IFeatureConfig>, IFeatureConfig>, BooleanSupplier> pair : list)
							if(pair.getLeft().test(feature, config)) {
								ConditionalConfiguredFeature<?, ?> conditional = new ConditionalConfiguredFeature<>(configuredFeature, pair.getRight());
								features.set(i, () -> conditional);
							}
					}
				}
			}
		}
	}

	public static void addGenerator(Module module, IGenerator generator, GenerationStage.Decoration stage, int weight) {
		WeightedGenerator weighted = new WeightedGenerator(module, generator, weight);
		if(!generators.containsKey(stage))
			generators.put(stage, new TreeSet<>());

		generators.get(stage).add(weighted);
	}

	public static void conditionalizeFeatures(GenerationStage.Decoration stage, BiPredicate<Feature<? extends IFeatureConfig>, IFeatureConfig> pred, BooleanSupplier condition) {
		if(!featureConditionalizers.containsKey(stage))
			featureConditionalizers.put(stage, new LinkedList<>());
		
		List<Pair<BiPredicate<Feature<? extends IFeatureConfig>, IFeatureConfig>, BooleanSupplier>> list = featureConditionalizers.get(stage);
		list.add(Pair.of(pred, condition));
	}

	public static void generateChunk(ISeedReader seedReader, ChunkGenerator generator, BlockPos pos, GenerationStage.Decoration stage) {
		if(!(seedReader instanceof WorldGenRegion))
			return;

		WorldGenRegion region = (WorldGenRegion) seedReader;
		SharedSeedRandom random = new SharedSeedRandom();
		long seed = random.setDecorationSeed(region.getSeed(), region.getMainChunkX() * 16, region.getMainChunkZ() * 16);
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
