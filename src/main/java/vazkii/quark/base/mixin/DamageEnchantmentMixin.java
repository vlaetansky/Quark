package vazkii.quark.base.mixin;

import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.quark.base.handler.AsmHooks;

@Mixin(DamageEnchantment.class)
public class DamageEnchantmentMixin {

	@Inject(method = "canApply", at = @At("RETURN"), cancellable = true)
	private void canSharpnessApply(ItemStack stack, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		if (!callbackInfoReturnable.getReturnValue()) {
			callbackInfoReturnable.setReturnValue(AsmHooks.canSharpnessApply(stack));
		}
	}
}
