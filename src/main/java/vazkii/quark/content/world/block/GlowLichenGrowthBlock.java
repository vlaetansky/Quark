package vazkii.quark.content.world.block;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.shapes.VoxelShape;
import vazkii.quark.base.block.QuarkBushBlock;
import vazkii.quark.base.module.QuarkModule;

public class GlowLichenGrowthBlock extends QuarkBushBlock {

	protected static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);
	
	public GlowLichenGrowthBlock(QuarkModule module) {
		super("glow_lichen_growth", module, CreativeModeTab.TAB_DECORATIONS, Properties.copy(Blocks.GLOW_LICHEN));
	}

}
