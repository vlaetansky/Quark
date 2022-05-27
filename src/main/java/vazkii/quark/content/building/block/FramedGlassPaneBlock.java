package vazkii.quark.content.building.block;

import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.block.QuarkInheritedPaneBlock;
import vazkii.quark.base.datagen.QuarkBlockStateProvider;

public class FramedGlassPaneBlock extends QuarkInheritedPaneBlock {

	public FramedGlassPaneBlock(IQuarkBlock parent) {
		super(parent);
	}

	@Override
	public void dataGen(QuarkBlockStateProvider states) {
		states.paneBlock(this, states.blockTexture(parent.getBlock()), states.modLoc("block/framed_glass_pane_top"));
		states.simpleItem(this, states.blockTexture(parent.getBlock()));
	}
}
