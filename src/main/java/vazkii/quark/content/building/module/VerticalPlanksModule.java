package vazkii.quark.content.building.module;

import net.minecraft.world.level.block.Block;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.util.VanillaWoods;
import vazkii.quark.base.util.VanillaWoods.Wood;
import vazkii.quark.content.building.block.VerticalPlanksBlock;

@LoadModule(category = ModuleCategory.BUILDING, antiOverlap = { "woodworks" })
public class VerticalPlanksModule extends QuarkModule {

	@Override
	public void register() {
		for(Wood type : VanillaWoods.ALL)
			add(type.name(), type.planks(), this);
	}

	public static QuarkBlock add(String name, Block base, QuarkModule module) {
		return new VerticalPlanksBlock("vertical_" + name + "_planks", module, base);
	}

}
