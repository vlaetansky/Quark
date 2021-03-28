package vazkii.quark.content.experimental.shiba.ai;

import java.util.EnumSet;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity.PickupStatus;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.pathfinding.PathNavigator;
import vazkii.quark.content.experimental.shiba.entity.ShibaEntity;

public class FetchArrowGoal extends Goal {

	final ShibaEntity shiba;
	private int timeToRecalcPath;
	private final PathNavigator navigator;
	int timeTilNextJump = 20;

	public FetchArrowGoal(ShibaEntity shiba) {
		this.shiba = shiba;
		this.navigator = shiba.getNavigator();

		this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	public void tick() {
		AbstractArrowEntity fetching = shiba.getFetching();
		if(fetching == null)
			return;

		this.shiba.getLookController().setLookPositionWithEntity(fetching, 10.0F, shiba.getVerticalFaceSpeed());
		if (--this.timeToRecalcPath <= 0) {
			this.timeToRecalcPath = 10;
			if (!shiba.getLeashed() && !shiba.isPassenger()) {
				this.navigator.tryMoveToEntityLiving(fetching, 1.1);
			}
		}

		double dist = shiba.getDistance(fetching);
		if(dist < 3 && fetching.isAlive()) {
			//			shiba.setMouthItem(shiba.fetching.getArrowStack());
			shiba.setMouthItem(new ItemStack(Items.ARROW)); // TODO
			fetching.remove();
		}

		timeTilNextJump--;
		if(timeTilNextJump <= 0) {
			timeTilNextJump = shiba.world.rand.nextInt(5) + 10;

			if(shiba.onGround) {
				shiba.addVelocity(0, 0.3, 0);
				shiba.setJumping(true);
			}
		}
	}

	@Override
	public boolean shouldContinueExecuting() {
		return shouldExecute();
	}

	@Override
	public boolean shouldExecute() {
		AbstractArrowEntity fetching = shiba.getFetching();
		return shiba.getMouthItem().isEmpty() && fetching != null && fetching.isAlive() && fetching.world == shiba.world && fetching.pickupStatus != PickupStatus.DISALLOWED;
	}

}
