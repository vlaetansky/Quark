package vazkii.quark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.item.enchantment.ArrowPiercingEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

@Mixin(ArrowPiercingEnchantment.class)
public class ArrowPiercingEnchantmentMixin {

	@Inject(method = "checkCompatibility", at = @At("RETURN"), cancellable = true)
	private void checkCompatibility(Enchantment enchantment, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		if(callbackInfoReturnable.getReturnValue())
			callbackInfoReturnable.setReturnValue(enchantment != Enchantments.BLOCK_EFFICIENCY);
	}
}
