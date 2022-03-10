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

	public QuarkBoatDispenseItemBehavior(String type) {
		this.type = type;
	}

	@Nonnull
	@Override
	public ItemStack execute(BlockSource world, @Nonnull ItemStack stack) {
		Direction direction = world.getBlockState().getValue(DispenserBlock.FACING);
		Level level = world.getLevel();
		double boatX = world.x() + (double)((float)direction.getStepX() * 1.125F);
		double boatY = world.y() + (double)((float)direction.getStepY() * 1.125F);
		double boatZ = world.z() + (double)((float)direction.getStepZ() * 1.125F);
		BlockPos pos = world.getPos().relative(direction);
		double offset;
		if (level.getFluidState(pos).is(FluidTags.WATER)) {
			offset = 1.0D;
		} else {
			if (!level.getBlockState(pos).isAir() || !level.getFluidState(pos.below()).is(FluidTags.WATER)) {
				return this.defaultDispenseItemBehavior.dispense(world, stack);
			}

			offset = 0.0D;
		}

		QuarkBoat boat = new QuarkBoat(level, boatX, boatY + offset, boatZ);
		boat.setQuarkBoatType(type);
		boat.setYRot(direction.toYRot());
		level.addFreshEntity(boat);
		stack.shrink(1);
		return stack;
	}

	@Override
	protected void playSound(BlockSource world) {
		world.getLevel().levelEvent(1000, world.getPos(), 0);
	}
}
