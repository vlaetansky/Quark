package vazkii.quark.content.building.module;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.CreativeModeTab;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.handler.VariantHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;

@LoadModule(category = ModuleCategory.BUILDING)
public class IronPlatesModule extends QuarkModule {
	
	@Override
	public void construct() {
		VariantHandler.addSlabAndStairs(new QuarkBlock("iron_plate", this, CreativeModeTab.TAB_BUILDING_BLOCKS, Block.Properties.copy(Blocks.IRON_BLOCK)));
		VariantHandler.addSlabAndStairs(new QuarkBlock("rusty_iron_plate", this, CreativeModeTab.TAB_BUILDING_BLOCKS, Block.Properties.copy(Blocks.IRON_BLOCK)));
	}

}
