package vazkii.quark.content.world.module;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.type.CompoundBiomeConfig;
import vazkii.quark.base.module.config.type.DimensionConfig;
import vazkii.quark.content.world.gen.structure.BigDungeonStructure;
import vazkii.quark.content.world.gen.structure.processor.BigDungeonChestProcessor;
import vazkii.quark.content.world.gen.structure.processor.BigDungeonSpawnerProcessor;
import vazkii.quark.content.world.gen.structure.processor.BigDungeonWaterProcessor;

// Mostly all taken from https://github.com/TelepathicGrunt/StructureTutorialMod/blob/1.18.2-Forge-Jigsaw/
// thank u <3

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

	@Config
	public static CompoundBiomeConfig biomeConfig = CompoundBiomeConfig.fromBiomeTypes(true, BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.BEACH, BiomeDictionary.Type.NETHER, BiomeDictionary.Type.END);

	@Config
	public static DimensionConfig dimensionConfig = DimensionConfig.overworld(false);
	
	private static BigDungeonChestProcessor CHEST_PROCESSOR = new BigDungeonChestProcessor();
	private static BigDungeonSpawnerProcessor SPAWN_PROCESSOR = new BigDungeonSpawnerProcessor();
	private static BigDungeonWaterProcessor WATER_PROCESSOR = new BigDungeonWaterProcessor();

	public static StructureProcessorType<BigDungeonChestProcessor> CHEST_PROCESSOR_TYPE = () -> Codec.unit(CHEST_PROCESSOR);
	public static StructureProcessorType<BigDungeonSpawnerProcessor> SPAWN_PROCESSOR_TYPE = () -> Codec.unit(SPAWN_PROCESSOR);
	public static StructureProcessorType<BigDungeonWaterProcessor> WATER_PROCESSOR_TYPE = () -> Codec.unit(WATER_PROCESSOR);

	public static final DeferredRegister<StructureFeature<?>> DEFERRED_REGISTRY_STRUCTURE = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, Quark.MOD_ID);
	public static final RegistryObject<StructureFeature<JigsawConfiguration>> STRUCTURE = DEFERRED_REGISTRY_STRUCTURE.register("mega_dungeon", () -> (new BigDungeonStructure(JigsawConfiguration.CODEC)));

	@Override
	public void setup() {
		enqueue(this::setupStructures);
	}

	private void setupStructures() {
		registerProcessor("big_dungeon_chest", CHEST_PROCESSOR, CHEST_PROCESSOR_TYPE);
		registerProcessor("big_dungeon_spawner", SPAWN_PROCESSOR, SPAWN_PROCESSOR_TYPE);
		registerProcessor("big_dungeon_water", WATER_PROCESSOR, WATER_PROCESSOR_TYPE);
	}
	
	private static <T extends StructureProcessor> void registerProcessor(String name, T processor, StructureProcessorType<T> type) {
		ResourceLocation res = new ResourceLocation(Quark.MOD_ID, name);
		Registry.register(Registry.STRUCTURE_PROCESSOR, res, type);
		Registry.register(BuiltinRegistries.PROCESSOR_LIST, res, new StructureProcessorList(Lists.newArrayList(processor)));
	}

}
