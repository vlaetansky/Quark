package vazkii.quark.content.building.block;

import net.minecraft.world.item.CreativeModeTab;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.datagen.QuarkBlockStateProvider;
import vazkii.quark.base.module.QuarkModule;

public class QuarkSandstoneBlock extends QuarkBlock {

	public QuarkSandstoneBlock(String regname, QuarkModule module, CreativeModeTab creativeTab, Properties properties) {
		super(regname, module, creativeTab, properties);
	}

	@Override
	public void dataGen(QuarkBlockStateProvider states) {
		// TODO
	}
}
