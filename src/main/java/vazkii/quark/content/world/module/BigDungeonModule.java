package vazkii.quark.content.world.module;

import com.google.common.collect.ImmutableSet;

import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.type.CompoundBiomeConfig;
import vazkii.quark.content.world.gen.structure.BigDungeonStructure;

@LoadModule(category = ModuleCategory.WORLD, hasSubscriptions = true)
public class BigDungeonModule extends QuarkModule {

	@Config(description = "The chance that a big dungeon spawn candidate will be allowed to spawn. 0.2 is 20%, which is the same as the Pillager Outpost.")
	public static double spawnChance = 0.1;

	@Config
	public static String lootTable = "minecraft:chests/simple_dungeon";

	@Config 
	public static int maxRooms = 10;

	@Config
	public static double chestChance = 0.5;

	@Config
	public static CompoundBiomeConfig biomeConfig = CompoundBiomeConfig.fromBiomeTypes(true, BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.BEACH, BiomeDictionary.Type.NETHER, BiomeDictionary.Type.END);

	public static final BigDungeonStructure STRUCTURE = new BigDungeonStructure(JigsawConfiguration.CODEC);
	private static ConfiguredStructureFeature<?, ?> feature;

	@Override
	public void construct() {
		//		new FloodFillItem(this);
		RegistryHelper.register(STRUCTURE);

		StructureFeature.STRUCTURES_REGISTRY.put(Quark.MOD_ID + ":big_dungeon", STRUCTURE);
	}

	@Override
	public void setup() {
		STRUCTURE.setup();	

		StructureFeatureConfiguration settings = new StructureFeatureConfiguration(20, 11, 79234823);

		ImmutableSet.of(NoiseGeneratorSettings.OVERWORLD, NoiseGeneratorSettings.AMPLIFIED, NoiseGeneratorSettings.NETHER, 
				NoiseGeneratorSettings.END, NoiseGeneratorSettings.CAVES, NoiseGeneratorSettings.FLOATING_ISLANDS)
		.stream()
		.map(BuiltinRegistries.NOISE_GENERATOR_SETTINGS::get)
		.map(NoiseGeneratorSettings::structureSettings)
		.map(StructureSettings::structureConfig) // get map
		.forEach(m -> m.put(STRUCTURE, settings));

		feature = STRUCTURE.configured(new JigsawConfiguration(() -> BigDungeonStructure.startPattern, maxRooms));
	}

	@SubscribeEvent
	public void onBiomeLoad(BiomeLoadingEvent event) {
		if(biomeConfig.canSpawn(event))
			event.getGeneration().getStructures().add(() -> feature);
	}

}
