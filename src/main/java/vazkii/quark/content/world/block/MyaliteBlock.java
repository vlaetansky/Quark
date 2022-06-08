package vazkii.quark.content.world.block;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.client.model.generators.ModelFile;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.datagen.QuarkBlockStateProvider;
import vazkii.quark.base.module.QuarkModule;

public class MyaliteBlock extends QuarkBlock implements IMyaliteColorProvider {

	public MyaliteBlock(String regname, QuarkModule module, CreativeModeTab creativeTab, Properties properties) {
		super(regname, module, creativeTab, properties);
	}

	@Override
	public void dataGen(QuarkBlockStateProvider states) {
		ModelFile file = states.models().singleTexture(getRegistryName().getPath(), states.modLoc("block/cube_all_tinted"), "all", states.blockTexture(this));
		states.simpleBlock(this, file);
		states.simpleBlockItem(this, file);
	}
}
