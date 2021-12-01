/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Aug 09, 2019, 09:59 AM (EST)]
 */
package vazkii.quark.content.mobs.ai;

import java.util.EnumSet;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class PassivePassengerGoal extends Goal {
	private final Mob entity;

	public PassivePassengerGoal(Mob entity) {
		this.entity = entity;
		setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP, Flag.TARGET));
	}

	@Override
	public boolean canUse() {
		return entity.isPassenger();
	}
}
