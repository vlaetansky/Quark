/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 13, 2019, 12:17 AM (EST)]
 */
package vazkii.quark.content.mobs.ai;

import java.util.EnumSet;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import vazkii.quark.content.mobs.entity.FoxhoundEntity;

public class SleepGoal extends Goal {

	private final FoxhoundEntity foxhound;
	private boolean isSleeping;
	private boolean wasSitting;

	public SleepGoal(FoxhoundEntity foxhound) {
		this.foxhound = foxhound;
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK, Flag.TARGET));
	}

	@Override
	public boolean canUse() {
		if (!this.foxhound.isTame() || this.foxhound.isInWater() || !this.foxhound.onGround)
			return false;
		else {
			LivingEntity living = this.foxhound.getOwner();

			if (living == null) return true;
			else
				return (!(this.foxhound.distanceToSqr(living) < 144.0D) || living.getLastHurtByMob() == null) && this.isSleeping;
		}
	}

	@Override
	public void start() {
		this.foxhound.getNavigation().stop();
		wasSitting = foxhound.isOrderedToSit(); 
		this.foxhound.setOrderedToSit(true); // setSitting
		this.foxhound.setInSittingPose(true);
	}

	@Override
	public void stop() {
		this.foxhound.setOrderedToSit(wasSitting); // setSitting
		this.foxhound.setInSittingPose(false);
	}

	public void setSleeping(boolean sitting) {
		this.isSleeping = sitting;
	}
}
