package vazkii.quark.content.building.module;

import net.minecraft.block.Blocks;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.building.block.HedgeBlock;

@LoadModule(category = ModuleCategory.BUILDING)
public class HedgesModule extends QuarkModule {

	@Override
	public void construct() {
		new HedgeBlock(this, Blocks.OAK_FENCE, Blocks.OAK_LEAVES);
		new HedgeBlock(this, Blocks.BIRCH_FENCE, Blocks.BIRCH_LEAVES);
		new HedgeBlock(this, Blocks.SPRUCE_FENCE, Blocks.SPRUCE_LEAVES);
		new HedgeBlock(this, Blocks.JUNGLE_FENCE, Blocks.JUNGLE_LEAVES);
		new HedgeBlock(this, Blocks.ACACIA_FENCE, Blocks.ACACIA_LEAVES);
		new HedgeBlock(this, Blocks.DARK_OAK_FENCE, Blocks.DARK_OAK_LEAVES);
	}
	
}
