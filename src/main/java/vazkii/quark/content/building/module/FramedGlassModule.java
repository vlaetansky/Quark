package vazkii.quark.content.building.module;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.ToolType;
import vazkii.quark.base.block.QuarkInheritedPaneBlock;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.building.block.FramedGlassBlock;

@LoadModule(category = ModuleCategory.BUILDING)
public class FramedGlassModule extends QuarkModule {

	@Override
	public void construct() {
		Block.Properties props = Block.Properties.of(Material.GLASS)
				.strength(3F, 10F)
				.sound(SoundType.GLASS)
				.harvestLevel(1)
				.harvestTool(ToolType.PICKAXE);
		
		new QuarkInheritedPaneBlock(new FramedGlassBlock("framed_glass", this, CreativeModeTab.TAB_BUILDING_BLOCKS, props, false));
		
		for(DyeColor dye : DyeColor.values())
			new QuarkInheritedPaneBlock(new FramedGlassBlock(dye.getName() + "_framed_glass", this, CreativeModeTab.TAB_BUILDING_BLOCKS, props, true));
	}

}
