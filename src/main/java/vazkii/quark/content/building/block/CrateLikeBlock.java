package vazkii.quark.content.building.block;

import net.minecraft.world.item.CreativeModeTab;
import vazkii.quark.base.block.QuarkFlammableBlock;
import vazkii.quark.base.datagen.QuarkBlockStateProvider;
import vazkii.quark.base.datagen.QuarkBlockTagsProvider;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.building.module.HedgesModule;

public class CrateLikeBlock extends QuarkFlammableBlock {

	public CrateLikeBlock(String regname, QuarkModule module, CreativeModeTab creativeTab, int flamability, Properties properties) {
		super(regname, module, creativeTab, flamability, properties);
	}

	@Override
	public void dataGen(QuarkBlockStateProvider states) {
		states.simpleBlock(this, states.models().cubeBottomTop(getRegistryName().getPath(),
			states.blockTexture(this), states.blockTexture(this, "_bottom"), states.blockTexture(this, "_top")));
		states.simpleBlockItem(this);
	}

	@Override
	public void dataGen(QuarkBlockTagsProvider blockTags) {
		blockTags.tag(HedgesModule.hedgesTag).add(this);
	}
}
