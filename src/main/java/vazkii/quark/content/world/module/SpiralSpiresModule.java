package vazkii.quark.content.world.module;

import net.minecraft.block.Block;
import net.minecraft.world.gen.GenerationStage.Decoration;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.world.WorldGenHandler;
import vazkii.quark.base.world.WorldGenWeights;
import vazkii.quark.base.world.config.DimensionConfig;
import vazkii.quark.base.world.config.StrictBiomeConfig;
import vazkii.quark.content.world.block.MyaliteCrystalBlock;
import vazkii.quark.content.world.gen.SpiralSpireGenerator;

@LoadModule(category = ModuleCategory.WORLD)
public class SpiralSpiresModule extends QuarkModule {

	@Config
	public static DimensionConfig dimensions = DimensionConfig.end(false);
	
	@Config
	public static StrictBiomeConfig biomes = new StrictBiomeConfig(false, "minecraft:end_highlands");
	
	@Config public static int rarity = 200;
	@Config public static int radius = 15;
	
	public static Block myalite_crystal;
	
	@Override
	public void construct() {
		myalite_crystal = new MyaliteCrystalBlock(this);
	}
	
	@Override
	public void setup() {
		WorldGenHandler.addGenerator(this, new SpiralSpireGenerator(dimensions), Decoration.SURFACE_STRUCTURES, WorldGenWeights.SPIRAL_SPIRES);
	}
	
}
