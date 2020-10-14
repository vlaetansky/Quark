package vazkii.quark.base.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.quark.base.handler.AsmHooks;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

	@Inject(method = "canApply", at = @At("RETURN"), cancellable = true)
	private void canApply(ItemStack stack, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		if (!callbackInfoReturnable.getReturnValue()) {
			Enchantment enchantment = (Enchantment) (Object) this;
			callbackInfoReturnable.setReturnValue(AsmHooks.canPiercingApply(enchantment, stack) || AsmHooks.canFortuneApply(enchantment, stack));
		}
	}
}
