package vazkii.quark.content.world.block;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Blocks;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.QuarkModule;

public class GlowMyceliumBlock extends QuarkBlock {

	public GlowMyceliumBlock(QuarkModule module) {
		super("glow_mycelium", module, CreativeModeTab.TAB_BUILDING_BLOCKS, 
				Properties.copy(Blocks.DEEPSLATE)
				.lightLevel(s -> 4));
	}

}
