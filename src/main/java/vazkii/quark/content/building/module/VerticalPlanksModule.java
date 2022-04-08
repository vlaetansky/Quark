package vazkii.quark.content.building.module;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.util.WoodTypes;
import vazkii.quark.base.util.WoodTypes.Wood;

@LoadModule(category = ModuleCategory.BUILDING)
public class VerticalPlanksModule extends QuarkModule {

	@Override
	public void register() {
		for(Wood type : WoodTypes.VANILLA)
			new QuarkBlock("vertical_" + type.name() + "_planks", this, CreativeModeTab.TAB_BUILDING_BLOCKS, Block.Properties.copy(type.planks()));
	}
	
}
