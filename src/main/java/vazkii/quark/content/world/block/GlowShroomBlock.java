package vazkii.quark.content.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import vazkii.quark.base.block.QuarkBushBlock;
import vazkii.quark.base.module.QuarkModule;

public class GlowShroomBlock extends QuarkBushBlock {

	protected static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);

	public GlowShroomBlock(QuarkModule module) {
		super("glow_shroom", module, CreativeModeTab.TAB_DECORATIONS, 
				Properties.copy(Blocks.RED_MUSHROOM)
				.lightLevel(s -> 10));
	}
	
	@Override // TODO for test only
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult res) {
		if(!level.isClientSide && hand == InteractionHand.MAIN_HAND)
			HugeGlowShroomBlock.place(level, level.random, pos);
		
		return InteractionResult.SUCCESS;
	}

	@Override
	public VoxelShape getShape(BlockState p_54889_, BlockGetter p_54890_, BlockPos p_54891_, CollisionContext p_54892_) {
		return SHAPE;
	}

	@Override
	protected boolean mayPlaceOn(BlockState p_54894_, BlockGetter p_54895_, BlockPos p_54896_) {
		return p_54894_.getBlock() == Blocks.DEEPSLATE;
	}

}
