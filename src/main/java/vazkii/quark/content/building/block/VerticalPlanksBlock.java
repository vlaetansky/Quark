package vazkii.quark.content.building.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import vazkii.quark.base.block.QuarkPlanksBlock;
import vazkii.quark.base.datagen.QuarkBlockStateProvider;
import vazkii.quark.base.datagen.QuarkBlockTagsProvider;
import vazkii.quark.base.datagen.QuarkItemTagsProvider;
import vazkii.quark.base.module.QuarkModule;

public class VerticalPlanksBlock extends QuarkPlanksBlock {

	private final Block base;

	public VerticalPlanksBlock(String name, QuarkModule module, Block base) {
		super(name, module, CreativeModeTab.TAB_DECORATIONS, Properties.copy(base));
		this.base = base;
	}

	@Override
	public void dataGen(QuarkBlockStateProvider states) {
		states.blockAndItem(this, states.models().singleTexture(getRegistryName().getPath(), states.modLoc("block/vertical_planks"), "all", states.blockTexture(base)));
	}
}
