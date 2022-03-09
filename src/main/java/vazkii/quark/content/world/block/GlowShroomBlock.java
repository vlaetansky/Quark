package vazkii.quark.content.world.block;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.base.block.QuarkBushBlock;
import vazkii.quark.base.module.QuarkModule;

import javax.annotation.Nonnull;

public class GlowShroomBlock extends QuarkBushBlock implements BonemealableBlock {

	protected static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);

	public GlowShroomBlock(QuarkModule module) {
		super("glow_shroom", module, CreativeModeTab.TAB_DECORATIONS,
				Properties.copy(Blocks.RED_MUSHROOM)
				.randomTicks()
				.lightLevel(s -> 10));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(@Nonnull BlockState stateIn, @Nonnull Level worldIn, @Nonnull BlockPos pos, @Nonnull Random rand) {
		super.animateTick(stateIn, worldIn, pos, rand);

		if(rand.nextInt(12) == 0 && worldIn.getBlockState(pos.above()).isAir())
			worldIn.addParticle(ParticleTypes.END_ROD,
					pos.getX() + 0.4 + rand.nextDouble() * 0.2,
					pos.getY() + 0.5 + rand.nextDouble() * 0.1,
					pos.getZ() + 0.4 + rand.nextDouble() * 0.2,
					(Math.random() - 0.5) * 0.04,
					(1 + Math.random()) * 0.02,
					(Math.random() - 0.5) * 0.04);
	}

	@Nonnull
	@Override
	public VoxelShape getShape(@Nonnull BlockState p_54889_, @Nonnull BlockGetter p_54890_, @Nonnull BlockPos p_54891_, @Nonnull CollisionContext p_54892_) {
		return SHAPE;
	}

	@Override
	protected boolean mayPlaceOn(BlockState p_54894_, @Nonnull BlockGetter p_54895_, @Nonnull BlockPos p_54896_) {
		return p_54894_.getBlock() == Blocks.DEEPSLATE;
	}

	@Override
	public boolean isValidBonemealTarget(@Nonnull BlockGetter p_50897_, @Nonnull BlockPos p_50898_, @Nonnull BlockState p_50899_, boolean p_50900_) {
		return true;
	}

	@Override
	public boolean isBonemealSuccess(@Nonnull Level p_50901_, Random p_50902_, @Nonnull BlockPos p_50903_, @Nonnull BlockState p_50904_) {
		return p_50902_.nextFloat() < 0.4D;
	}

	@Override
	public void performBonemeal(@Nonnull ServerLevel p_50893_, @Nonnull Random p_50894_, @Nonnull BlockPos p_50895_, @Nonnull BlockState p_50896_) {
		HugeGlowShroomBlock.place(p_50893_, p_50894_, p_50895_);
	}

}
