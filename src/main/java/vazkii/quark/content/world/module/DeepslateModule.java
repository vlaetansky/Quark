package vazkii.quark.content.world.module;

import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.world.gen.GenerationStage.Decoration;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.block.QuarkPillarBlock;
import vazkii.quark.base.handler.VariantHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.type.DimensionConfig;
import vazkii.quark.base.world.WorldGenHandler;
import vazkii.quark.base.world.WorldGenWeights;
import vazkii.quark.content.world.gen.DeepslateSheetGenerator;

@LoadModule(category = ModuleCategory.WORLD)
public class DeepslateModule extends QuarkModule {

	@Config public static DimensionConfig dimensions = DimensionConfig.overworld(false);
	@Config public static int sheetHeight = 12;
	@Config public static int sheetYStart = 0;
	
	public static Block deepslate, smooth_basalt;
	
	@Override
	public void construct() {
		Properties deepslateProps = Properties.create(Material.ROCK, MaterialColor.GRAY).hardnessAndResistance(3F, 6F).setRequiresTool();
		Properties deepslateVariantProps = Properties.create(Material.ROCK, MaterialColor.GRAY).hardnessAndResistance(3.5F, 6F).setRequiresTool();
		Properties basaltProps = Properties.create(Material.ROCK, MaterialColor.BLACK).hardnessAndResistance(1.25F, 4.2F).sound(SoundType.BASALT).setRequiresTool();
		
		deepslate = new QuarkPillarBlock("deepslate", this, ItemGroup.BUILDING_BLOCKS, deepslateProps);

		VariantHandler.addSlabStairsWall(new QuarkBlock("cobbled_deepslate", this, ItemGroup.BUILDING_BLOCKS, deepslateVariantProps));
		VariantHandler.addSlabStairsWall(new QuarkBlock("polished_deepslate", this, ItemGroup.BUILDING_BLOCKS, deepslateVariantProps));
		VariantHandler.addSlabStairsWall(new QuarkBlock("deepslate_bricks", this, ItemGroup.BUILDING_BLOCKS, deepslateVariantProps));
		VariantHandler.addSlabStairsWall(new QuarkBlock("deepslate_tiles", this, ItemGroup.BUILDING_BLOCKS, deepslateVariantProps));
		new QuarkBlock("chiseled_deepslate", this, ItemGroup.BUILDING_BLOCKS, deepslateVariantProps);
		
		smooth_basalt = VariantHandler.addSlabStairsWall(new QuarkBlock("smooth_basalt", this, ItemGroup.BUILDING_BLOCKS, basaltProps));
	}
	
	@Override
	public void setup() {
		WorldGenHandler.addGenerator(this, new DeepslateSheetGenerator(dimensions), Decoration.UNDERGROUND_DECORATION, WorldGenWeights.DEEPSLATE_SHEETS);
	}
	
}
