package vazkii.quark.content.building.module;

import java.util.function.BooleanSupplier;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.handler.VariantHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = ModuleCategory.BUILDING)
public class MoreBrickTypesModule extends QuarkModule {

	@Config(flag = "blue_nether_bricks",
			description = "This also comes with a utility recipe for Red Nether Bricks") 
	public boolean enableBlueNetherBricks = true;

	@Config(flag = "sandstone_bricks",
			description = "This also includes Red Sandstone Bricks and Soul Sandstone Bricks") 
	public boolean enableSandstoneBricks = true;
	
	@Override
	public void construct() {
		add("blue_nether", Blocks.NETHER_BRICKS, () -> enableBlueNetherBricks);
		add("sandstone", Blocks.SANDSTONE, () -> enableSandstoneBricks);
		add("red_sandstone", Blocks.RED_SANDSTONE, () -> enableSandstoneBricks);
		add("soul_sandstone", Blocks.SANDSTONE, () -> enableSandstoneBricks && ModuleLoader.INSTANCE.isModuleEnabled(SoulSandstoneModule.class));
	}
	
	private void add(String name, Block parent, BooleanSupplier cond) {
		VariantHandler.addSlabStairsWall(new QuarkBlock(name + "_bricks", this, CreativeModeTab.TAB_BUILDING_BLOCKS, 
				Block.Properties.copy(parent)
				.strength(2F, 6F)
				.requiresCorrectToolForDrops())
//				.harvestTool(ToolType.PICKAXE)) TODO tag
				.setCondition(cond));
	}
	
}

