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

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import vazkii.quark.content.mobs.entity.Foxhound;

public class FindPlaceToSleepGoal extends MoveToBlockGoal {
	private final Foxhound foxhound;

	private final boolean furnaceOnly;

	private boolean hadSlept = false;

	public FindPlaceToSleepGoal(Foxhound foxhound, double speed, boolean furnaceOnly) {
		super(foxhound, speed, 8);
		this.foxhound = foxhound;
		this.furnaceOnly = furnaceOnly;
	}

	@Override
	public boolean canUse() {
		return this.foxhound.isTame() && !this.foxhound.isOrderedToSit() && super.canUse();
	}

	@Override
	public boolean canContinueToUse() {
		return (!hadSlept || this.foxhound.isSleeping()) && super.canContinueToUse();
	}

	@Override
	public void start() {
		super.start();
		hadSlept = false;
		this.foxhound.setOrderedToSit(false); // setSitting
		this.foxhound.getSleepGoal().setSleeping(false);
		this.foxhound.setInSittingPose(false);
	}

	@Override
	public void stop() {
		super.stop();
		hadSlept = false;
		this.foxhound.setOrderedToSit(false); // setSitting
		this.foxhound.getSleepGoal().setSleeping(false);
		this.foxhound.setInSittingPose(false);
	}

	@Override
	public void tick() {
		super.tick();

		Vec3 motion = foxhound.getDeltaMovement();

		if (!this.isReachedTarget() || motion.x > 0 || motion.z > 0) {
			this.foxhound.setOrderedToSit(false); // setSitting
			this.foxhound.getSleepGoal().setSleeping(false);
			this.foxhound.setInSittingPose(false);
		} else if (!this.foxhound.isOrderedToSit()) {
			this.foxhound.setOrderedToSit(true); // setSitting
			this.foxhound.getSleepGoal().setSleeping(true);
			this.foxhound.setInSittingPose(true);
			foxhound.startSleeping(blockPos.above());
			hadSlept = true;
		}
	}

	@Override
	protected boolean isValidTarget(@Nonnull LevelReader world, @Nonnull BlockPos pos) {
		if (!world.isEmptyBlock(pos.above())) {
			return false;
		} else {
			BlockState state = world.getBlockState(pos);
			BlockEntity tileentity = world.getBlockEntity(pos);

			if(furnaceOnly)
				return tileentity instanceof FurnaceBlockEntity;

			return state.getLightEmission(world, pos) > 2;
		}
	}
}
