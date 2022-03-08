package vazkii.quark.base.world;

import java.util.Arrays;
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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
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
				ConfiguredFeature<?, ?> feature = new ConfiguredFeature<>(defersBaseFeature.get(stage), FeatureConfiguration.NONE);

				ResourceLocation resloc = new ResourceLocation(Quark.MOD_ID, "deferred_feature_" + stage.name().toLowerCase(Locale.ROOT));
				Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, resloc, feature);
				
				PlacedFeature placed = new PlacedFeature(Holder.direct(feature), Arrays.asList(CHUNK_CORNER_PLACEMENT));
				Registry.register(BuiltinRegistries.PLACED_FEATURE, resloc, placed);
				
				defers.put(stage, placed);
			}
			
			Registry.register(Registry.PLACEMENT_MODIFIERS, new ResourceLocation(Quark.MOD_ID, "chunk_corner"), CHUNK_CORNER_PLACEMENT_TYPE);
		});
	}

	@SubscribeEvent
	public static void onBiomesLoaded(BiomeLoadingEvent ev) {
		BiomeGenerationSettingsBuilder settings = ev.getGeneration();

		for(GenerationStep.Decoration stage : GenerationStep.Decoration.values()) {
			List<Holder<PlacedFeature>> features = settings.getFeatures(stage);
			features.add(Holder.direct(defers.get(stage)));
		}
	}

	public static void addGenerator(QuarkModule module, IGenerator generator, GenerationStep.Decoration stage, int weight) {
		WeightedGenerator weighted = new WeightedGenerator(module, generator, weight);
		if(!generators.containsKey(stage))
			generators.put(stage, new TreeSet<>());

		generators.get(stage).add(weighted);
	}

	public static void generateChunk(FeaturePlaceContext<NoneFeatureConfiguration> context, GenerationStep.Decoration stage) {
		WorldGenLevel level = context.level();
		if(!(level instanceof WorldGenRegion))
			return;

		ChunkGenerator generator = context.chunkGenerator();
		BlockPos origin = context.origin();
		BlockPos pos = new BlockPos(origin.getX(), 0, origin.getZ());
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
