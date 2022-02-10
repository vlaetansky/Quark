package vazkii.quark.content.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import vazkii.quark.base.block.QuarkBushBlock;
import vazkii.quark.base.module.QuarkModule;

public class GlowLichenGrowthBlock extends QuarkBushBlock {

	protected static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);
	
	public GlowLichenGrowthBlock(QuarkModule module) {
		super("glow_lichen_growth", module, CreativeModeTab.TAB_DECORATIONS, 
				Properties.copy(Blocks.GLOW_LICHEN)
				.lightLevel(s -> 8));
	}

	@Override
	public VoxelShape getShape(BlockState p_54889_, BlockGetter p_54890_, BlockPos p_54891_, CollisionContext p_54892_) {
		return SHAPE;
	}

	@Override
	protected boolean mayPlaceOn(BlockState p_54894_, BlockGetter p_54895_, BlockPos p_54896_) {
		return p_54894_.isFaceSturdy(p_54895_, p_54896_.below(), Direction.UP);
	}
	
}
