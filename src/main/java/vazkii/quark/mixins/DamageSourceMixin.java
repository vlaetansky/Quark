package vazkii.quark.mixins;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.quark.base.handler.AsmHooks;

@Mixin(DamageSource.class)
public class DamageSourceMixin {

	@Inject(method = "causePlayerDamage", at = @At("HEAD"), cancellable = true)
	private static void causePlayerDamage(PlayerEntity player, CallbackInfoReturnable<DamageSource> callbackInfoReturnable) {
		DamageSource damage = AsmHooks.createPlayerDamage(player);

		if (damage != null) {
			callbackInfoReturnable.setReturnValue(damage);
		}
	}
}
