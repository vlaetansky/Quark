package vazkii.quark.content.mobs.ai;

import java.lang.reflect.Method;
import java.util.EnumSet;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import vazkii.quark.content.mobs.entity.Shiba;

public class FetchArrowGoal extends Goal {

	final Shiba shiba;
	private int timeToRecalcPath;
	private final PathNavigation navigator;
	int timeTilNextJump = 20;

	public FetchArrowGoal(Shiba shiba) {
		this.shiba = shiba;
		this.navigator = shiba.getNavigation();

		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	@Override
	public void tick() {
		AbstractArrow fetching = shiba.getFetching();
		if(fetching == null)
			return;

		this.shiba.getLookControl().setLookAt(fetching, 10.0F, shiba.getMaxHeadXRot());
		if (--this.timeToRecalcPath <= 0) {
			this.timeToRecalcPath = 10;
			if (!shiba.isLeashed() && !shiba.isPassenger()) {
				this.navigator.moveTo(fetching, 1.1);
			}
		}

		double dist = shiba.distanceTo(fetching);
		if(dist < 3 && fetching.isAlive()) {
			try {
				// getArrowStack is non AT-able
				Method m = ObfuscationReflectionHelper.findMethod(fetching.getClass(), "m_7941_"); // getPickupItem
				m.setAccessible(true);
				ItemStack stack = (ItemStack) m.invoke(fetching);
				shiba.setMouthItem(stack);
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
			}
			fetching.discard();
		}

		timeTilNextJump--;
		if(timeTilNextJump <= 0) {
			timeTilNextJump = shiba.level.random.nextInt(5) + 10;

			if(shiba.onGround) {
				shiba.push(0, 0.3, 0);
				shiba.setJumping(true);
			}
		}
	}

	@Override
	public boolean canContinueToUse() {
		return canUse();
	}

	@Override
	public boolean canUse() {
		AbstractArrow fetching = shiba.getFetching();
		return shiba.getMouthItem().isEmpty() && fetching != null && fetching.isAlive() && fetching.level == shiba.level && fetching.pickup != Pickup.DISALLOWED;
	}

}
