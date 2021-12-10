package vazkii.quark.content.world.module;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.PlainVillagePools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.type.CompoundBiomeConfig;
import vazkii.quark.content.world.gen.structure.BigDungeonStructure;
import vazkii.quark.content.world.gen.structure.processor.BigDungeonChestProcessor;
import vazkii.quark.content.world.gen.structure.processor.BigDungeonSpawnerProcessor;
import vazkii.quark.content.world.gen.structure.processor.BigDungeonWaterProcessor;

@LoadModule(category = ModuleCategory.WORLD, hasSubscriptions = true)
public class BigDungeonModule extends QuarkModule {

	@Config(description = "The chance that a big dungeon spawn candidate will be allowed to spawn. 0.2 is 20%, which is the same as the Pillager Outpost.")
	public static double spawnChance = 0.1;

	@Config
	public static String lootTable = "minecraft:chests/simple_dungeon";

	@Config public static int maxRooms = 10;
	@Config public static int minStartY = -10;
	@Config public static int maxStartY = 10;
	@Config public static double chestChance = 0.5;
	@Config public static int averageDistance = 20;
	@Config public static int minimumDistance = 11;

	@Config
	public static CompoundBiomeConfig biomeConfig = CompoundBiomeConfig.fromBiomeTypes(true, BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.BEACH, BiomeDictionary.Type.NETHER, BiomeDictionary.Type.END);

	private static BigDungeonChestProcessor CHEST_PROCESSOR = new BigDungeonChestProcessor();
	private static BigDungeonSpawnerProcessor SPAWN_PROCESSOR = new BigDungeonSpawnerProcessor();
	private static BigDungeonWaterProcessor WATER_PROCESSOR = new BigDungeonWaterProcessor();

	public static StructureProcessorType<BigDungeonChestProcessor> CHEST_PROCESSOR_TYPE = () -> Codec.unit(CHEST_PROCESSOR);
	public static StructureProcessorType<BigDungeonSpawnerProcessor> SPAWN_PROCESSOR_TYPE = () -> Codec.unit(SPAWN_PROCESSOR);
	public static StructureProcessorType<BigDungeonWaterProcessor> WATER_PROCESSOR_TYPE = () -> Codec.unit(WATER_PROCESSOR);

	// ============================================================================================================================================================================
	// All the nonsense below comes from TelepathicGrunt's Structure Tutorial
	// https://github.com/TelepathicGrunt/StructureTutorialMod/tree/1.18.x-Forge-Jigsaw/
	// ============================================================================================================================================================================

	public static final DeferredRegister<StructureFeature<?>> DEFERRED_REGISTRY_STRUCTURE = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, Quark.MOD_ID);
	public static final RegistryObject<StructureFeature<JigsawConfiguration>> STRUCTURE = DEFERRED_REGISTRY_STRUCTURE.register("mega_dungeon", () -> (new BigDungeonStructure(JigsawConfiguration.CODEC)));
	public static Lazy<ConfiguredStructureFeature<?, ?>> configured = Lazy.of(() -> STRUCTURE.get().configured(new JigsawConfiguration(() -> PlainVillagePools.START, 0)));

	@Override
	public void construct() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		DEFERRED_REGISTRY_STRUCTURE.register(modEventBus);
	}
	
	@Override
	public void setup() {
		enqueue(this::setupStructures);
	}

	private void setupStructures() {
		registerProcessor("big_dungeon_chest", CHEST_PROCESSOR, CHEST_PROCESSOR_TYPE);
		registerProcessor("big_dungeon_spawner", SPAWN_PROCESSOR, SPAWN_PROCESSOR_TYPE);
		registerProcessor("big_dungeon_water", WATER_PROCESSOR, WATER_PROCESSOR_TYPE);
		
		setupMapSpacingAndLand(STRUCTURE.get(), new StructureFeatureConfiguration(averageDistance, minimumDistance, 79234823));

		Registry.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, new ResourceLocation(Quark.MOD_ID, "configured_mega_dungeon"), configured.get());
	}
	
	private static <T extends StructureProcessor> void registerProcessor(String name, T processor, StructureProcessorType<T> type) {
		ResourceLocation res = new ResourceLocation(Quark.MOD_ID, name);
		Registry.register(Registry.STRUCTURE_PROCESSOR, res, type);
		Registry.register(BuiltinRegistries.PROCESSOR_LIST, res, new StructureProcessorList(Lists.newArrayList(processor)));
	}

	public static <F extends StructureFeature<?>> void setupMapSpacingAndLand(F structure, StructureFeatureConfiguration structureFeatureConfiguration) {
		StructureFeature.STRUCTURES_REGISTRY.put(structure.getRegistryName().toString(), structure);

		StructureSettings.DEFAULTS = ImmutableMap.<StructureFeature<?>, StructureFeatureConfiguration>builder().putAll(StructureSettings.DEFAULTS).put(structure, structureFeatureConfiguration).build();

		BuiltinRegistries.NOISE_GENERATOR_SETTINGS.entrySet().forEach(settings -> {
			Map<StructureFeature<?>, StructureFeatureConfiguration> structureMap = settings.getValue().structureSettings().structureConfig();

			if(structureMap instanceof ImmutableMap) {
				Map<StructureFeature<?>, StructureFeatureConfiguration> tempMap = new HashMap<>(structureMap);
				tempMap.put(structure, structureFeatureConfiguration);
				settings.getValue().structureSettings().structureConfig = tempMap;
			}
			else
				structureMap.put(structure, structureFeatureConfiguration);
		});
	}

	@SubscribeEvent
	public void addDimensionalSpacing(final WorldEvent.Load event) {
		if(event.getWorld() instanceof ServerLevel serverLevel){
			ChunkGenerator chunkGenerator = serverLevel.getChunkSource().getGenerator();
			if(chunkGenerator instanceof FlatLevelSource && serverLevel.dimension().equals(Level.OVERWORLD))
				return;

			StructureSettings worldStructureConfig = chunkGenerator.getSettings();
			HashMap<StructureFeature<?>, HashMultimap<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>>> STStructureToMultiMap = new HashMap<>();

			for(Map.Entry<ResourceKey<Biome>, Biome> biomeEntry : serverLevel.registryAccess().ownedRegistryOrThrow(Registry.BIOME_REGISTRY).entrySet()) {
				if(biomeConfig.canSpawn(biomeEntry.getValue()))
					associateBiomeToConfiguredStructure(STStructureToMultiMap, configured.get(), biomeEntry.getKey());
			}

			ImmutableMap.Builder<StructureFeature<?>, ImmutableMultimap<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>>> tempStructureToMultiMap = ImmutableMap.builder();
			worldStructureConfig.configuredStructures.entrySet().stream().filter(entry -> !STStructureToMultiMap.containsKey(entry.getKey())).forEach(tempStructureToMultiMap::put);

			STStructureToMultiMap.forEach((key, value) -> tempStructureToMultiMap.put(key, ImmutableMultimap.copyOf(value)));
			worldStructureConfig.configuredStructures = tempStructureToMultiMap.build();

			Map<StructureFeature<?>, StructureFeatureConfiguration> tempMap = new HashMap<>(worldStructureConfig.structureConfig());
			tempMap.putIfAbsent(STRUCTURE.get(), StructureSettings.DEFAULTS.get(STRUCTURE.get()));
			worldStructureConfig.structureConfig = tempMap;
		}
	}

	private static void associateBiomeToConfiguredStructure(Map<StructureFeature<?>, HashMultimap<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>>> STStructureToMultiMap, ConfiguredStructureFeature<?, ?> configuredStructureFeature, ResourceKey<Biome> biomeRegistryKey) {
		STStructureToMultiMap.putIfAbsent(configuredStructureFeature.feature, HashMultimap.create());
		HashMultimap<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>> configuredStructureToBiomeMultiMap = STStructureToMultiMap.get(configuredStructureFeature.feature);
		if(configuredStructureToBiomeMultiMap.containsValue(biomeRegistryKey))
			Quark.LOG.error("""
					Detected 2 ConfiguredStructureFeatures that share the same base StructureFeature trying to be added to same biome. One will be prevented from spawning.
					This issue happens with vanilla too and is why a Snowy Village and Plains Village cannot spawn in the same biome because they both use the Village base structure.
					The two conflicting ConfiguredStructures are: {}, {}
					The biome that is attempting to be shared: {}
					""",
					BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(configuredStructureFeature),
					BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(configuredStructureToBiomeMultiMap.entries().stream().filter(e -> e.getValue() == biomeRegistryKey).findFirst().get().getKey()),
					biomeRegistryKey);

		else configuredStructureToBiomeMultiMap.put(configuredStructureFeature, biomeRegistryKey);
	}

}
