package vazkii.quark.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.player.Player;
import vazkii.quark.content.automation.module.FeedingTroughModule;

@Mixin(TemptGoal.class)
public class TemptGoalMixin {

	@Shadow
	protected Player player;

	@Shadow
	@Final
	public PathfinderMob mob;

	@Inject(method = "canUse", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/ai/goal/TemptGoal;player:Lnet/minecraft/world/entity/player/Player;", ordinal = 0, shift = At.Shift.AFTER))
	private void findTroughs(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		if (mob.level instanceof ServerLevel level)
			player = FeedingTroughModule.temptWithTroughs((TemptGoal) (Object) this, player, level);
	}
}
