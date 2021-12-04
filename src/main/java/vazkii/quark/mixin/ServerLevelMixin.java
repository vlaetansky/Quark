package vazkii.quark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import vazkii.quark.content.mobs.entity.CrabEntity;
import vazkii.quark.content.tweaks.module.ImprovedSleepingModule;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

	@Inject(method = "updateSleepingPlayerList", at = @At(value = "HEAD"), cancellable = true)
	private void updateSleepingPlayerList(CallbackInfo callbackInfo) {
		if(ImprovedSleepingModule.shouldCancelVanillaCheck())
			callbackInfo.cancel();
	}

	@Inject(method = "levelEvent", at = @At("HEAD"))
	private void rave(Player player, int type, BlockPos pos, int data, CallbackInfo callbackInfo) {
		if(type == 1010)
			CrabEntity.rave((ServerLevel) (Object) this, pos, data != 0);
	}
}
