package vazkii.quark.content.building.module;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.block.QuarkPillarBlock;
import vazkii.quark.base.handler.VariantHandler;
import vazkii.quark.base.item.QuarkItem;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;

@LoadModule(category = ModuleCategory.BUILDING)
public class MidoriModule extends QuarkModule {

	@Override
	public void construct() {
		new QuarkItem("moss_paste", this, new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS));
		
		Block.Properties props = Block.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GREEN)
				.requiresCorrectToolForDrops()
//        		.harvestTool(ToolType.PICKAXE) TODO TAG
        		.strength(1.5F, 6.0F);
		
		VariantHandler.addSlabAndStairs(new QuarkBlock("midori_block", this, CreativeModeTab.TAB_BUILDING_BLOCKS, props));
		new QuarkPillarBlock("midori_pillar", this, CreativeModeTab.TAB_BUILDING_BLOCKS, props);
	}
	
}
