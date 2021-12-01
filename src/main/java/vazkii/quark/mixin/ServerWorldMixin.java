package vazkii.quark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import vazkii.quark.content.mobs.entity.CrabEntity;
import vazkii.quark.content.tweaks.module.ImprovedSleepingModule;

@Mixin(ServerLevel.class)
public class ServerWorldMixin {

	@Shadow
	private boolean allPlayersSleeping;

	@Inject(method = "updateAllPlayersSleepingFlag", at = @At(value = "FIELD", target = "Lnet/minecraft/world/server/ServerWorld;allPlayersSleeping:Z", ordinal = 1, shift = At.Shift.AFTER))
	private void updateAllPlayersSleepingFlag(CallbackInfo callbackInfo) {
		allPlayersSleeping = ImprovedSleepingModule.isEveryoneAsleep(allPlayersSleeping);
	}

	@Inject(method = "playEvent", at = @At("HEAD"))
	private void rave(Player player, int type, BlockPos pos, int data, CallbackInfo callbackInfo) {
		if(type == 1010)
			CrabEntity.rave((ServerLevel) (Object) this, pos, data != 0);
	}
}
