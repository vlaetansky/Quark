package vazkii.quark.content.building.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.datagen.QuarkBlockStateProvider;
import vazkii.quark.base.module.QuarkModule;

public class QuarkSmoothSandstoneBlock extends QuarkBlock {

	public QuarkSmoothSandstoneBlock(String regname, QuarkModule module, CreativeModeTab creativeTab, Properties properties) {
		super(regname, module, creativeTab, properties);
	}

	@Override
	public ResourceLocation blockTexture(QuarkBlockStateProvider states) {
		return states.modLoc("block/" + getRegistryName().getPath().replace("smooth_", "") + "_top");
	}
}
