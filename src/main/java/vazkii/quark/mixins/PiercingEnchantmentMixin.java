package vazkii.quark.mixins;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.PiercingEnchantment;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.quark.base.handler.AsmHooks;

@Mixin(PiercingEnchantment.class)
public class PiercingEnchantmentMixin {

	@Inject(method = "canApplyTogether", at = @At("RETURN"), cancellable = true)
	private void canApplyTogether(Enchantment enchantment, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		if (callbackInfoReturnable.getReturnValue()) {
			callbackInfoReturnable.setReturnValue(AsmHooks.isNotEfficiency(enchantment));
		}
	}
}
