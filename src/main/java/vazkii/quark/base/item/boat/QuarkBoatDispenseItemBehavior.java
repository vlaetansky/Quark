package vazkii.quark.base.item.boat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

import javax.annotation.Nonnull;

// Pretty much just a copy of BoatDispenseItemBehavior but for the quark boat
public class QuarkBoatDispenseItemBehavior extends DefaultDispenseItemBehavior {

	private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
	private final String type;

	public QuarkBoatDispenseItemBehavior(String p_123371_) {
		this.type = p_123371_;
	}

	@Nonnull
	@Override
	public ItemStack execute(BlockSource p_123375_, @Nonnull ItemStack p_123376_) {
		Direction direction = p_123375_.getBlockState().getValue(DispenserBlock.FACING);
		Level level = p_123375_.getLevel();
		double d0 = p_123375_.x() + (double)((float)direction.getStepX() * 1.125F);
		double d1 = p_123375_.y() + (double)((float)direction.getStepY() * 1.125F);
		double d2 = p_123375_.z() + (double)((float)direction.getStepZ() * 1.125F);
		BlockPos blockpos = p_123375_.getPos().relative(direction);
		double d3;
		if (level.getFluidState(blockpos).is(FluidTags.WATER)) {
			d3 = 1.0D;
		} else {
			if (!level.getBlockState(blockpos).isAir() || !level.getFluidState(blockpos.below()).is(FluidTags.WATER)) {
				return this.defaultDispenseItemBehavior.dispense(p_123375_, p_123376_);
			}

			d3 = 0.0D;
		}

		QuarkBoat boat = new QuarkBoat(level, d0, d1 + d3, d2);
		boat.setQuarkBoatType(type);
		boat.setYRot(direction.toYRot());
		level.addFreshEntity(boat);
		p_123376_.shrink(1);
		return p_123376_;
	}

	@Override
	protected void playSound(BlockSource p_123373_) {
		p_123373_.getLevel().levelEvent(1000, p_123373_.getPos(), 0);
	}
}
