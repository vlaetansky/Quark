package vazkii.quark.content.building.module;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.CreativeModeTab;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.handler.VariantHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = ModuleCategory.BUILDING)
public class CobblestoneBricksModule extends QuarkModule {

	@Config(flag = "blackstone_bricks")
	private static boolean enableBlackstoneBricks = true;
	
	@Config(flag = "dirt_bricks")
	private static boolean enableDirtBricks = true;
	
	@Config(flag = "netherrack_bricks")
	private static boolean enableNetherrackBricks = true;
	
	@Override
	public void construct() {
		VariantHandler.addSlabStairsWall(new QuarkBlock("cobblestone_bricks", this, CreativeModeTab.TAB_BUILDING_BLOCKS, Block.Properties.copy(Blocks.COBBLESTONE)));
		VariantHandler.addSlabStairsWall(new QuarkBlock("mossy_cobblestone_bricks", this, CreativeModeTab.TAB_BUILDING_BLOCKS, Block.Properties.copy(Blocks.MOSSY_COBBLESTONE)));
		
		VariantHandler.addSlabStairsWall(new QuarkBlock("blackstone_bricks", this, CreativeModeTab.TAB_BUILDING_BLOCKS, Block.Properties.copy(Blocks.BLACKSTONE)).setCondition(() -> enableBlackstoneBricks));
		VariantHandler.addSlabStairsWall(new QuarkBlock("dirt_bricks", this, CreativeModeTab.TAB_BUILDING_BLOCKS, Block.Properties.copy(Blocks.DIRT)).setCondition(() -> enableDirtBricks));
		VariantHandler.addSlabStairsWall(new QuarkBlock("netherrack_bricks", this, CreativeModeTab.TAB_BUILDING_BLOCKS, Block.Properties.copy(Blocks.NETHERRACK)).setCondition(() -> enableNetherrackBricks));
	}
	
}
