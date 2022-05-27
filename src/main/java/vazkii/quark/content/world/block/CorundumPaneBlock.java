package vazkii.quark.content.world.block;

import net.minecraft.resources.ResourceLocation;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.block.QuarkInheritedPaneBlock;
import vazkii.quark.base.datagen.QuarkBlockStateProvider;

public class CorundumPaneBlock extends QuarkInheritedPaneBlock {
	public CorundumPaneBlock(IQuarkBlock parent) {
		super(parent);
	}

	@Override
	public void dataGen(QuarkBlockStateProvider states) {
		ResourceLocation loc = states.blockTexture(parent.getBlock());
		states.paneBlock(this, loc, loc);
		states.simpleItem(this, loc);
	}
}
