package vazkii.quark.base.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.CreativeModeTab;
import vazkii.quark.base.datagen.QuarkBlockTagsProvider;
import vazkii.quark.base.datagen.QuarkItemTagsProvider;
import vazkii.quark.base.module.QuarkModule;

public class QuarkPlanksBlock extends QuarkBlock {

	public QuarkPlanksBlock(String regname, QuarkModule module, CreativeModeTab creativeTab, Properties properties) {
		super(regname, module, creativeTab, properties);
	}

	@Override
	public void dataGen(QuarkItemTagsProvider itemTags) {
		itemTags.copyInto(BlockTags.PLANKS, ItemTags.PLANKS);
	}

	@Override
	public void dataGen(QuarkBlockTagsProvider blockTags) {
		blockTags.tag(BlockTags.PLANKS).add(this);
	}
}
