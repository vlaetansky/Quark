package vazkii.quark.content.building.module;

import net.minecraft.item.DyeColor;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.building.block.StoolBlock;

@LoadModule(category = ModuleCategory.BUILDING)
public class StoolsModule extends QuarkModule {

	@Override
	public void construct() {
		for(DyeColor dye : DyeColor.values())
			new StoolBlock(this, dye);
	}

}
