package vazkii.quark.content.experimental.shiba.ai;

import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import vazkii.quark.content.experimental.shiba.entity.ShibaEntity;

public class DeliverFetchedItemGoal extends FollowOwnerGoal {

	final ShibaEntity shiba;
	int timeTilNextJump = 20;
	
	public DeliverFetchedItemGoal(ShibaEntity shiba, double speed, float minDist, float maxDist, boolean teleportToLeaves) {
		super(shiba, speed, minDist, maxDist, teleportToLeaves);
		this.shiba = shiba;
	}
	
	@Override
	public void tick() {
		super.tick();
		
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
	public boolean canUse() {
		return super.canUse() && !shiba.getMouthItem().isEmpty();
	}
	
	@Override
	public boolean canContinueToUse() {
		return super.canContinueToUse() && !shiba.getMouthItem().isEmpty();
	}

}
