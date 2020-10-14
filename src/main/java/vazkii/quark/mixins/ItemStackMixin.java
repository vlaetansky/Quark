package vazkii.quark.mixins;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.quark.base.handler.AsmHooks;

@Mixin(ItemStack.class)
public class ItemStackMixin {

	@Inject(method = "getTextComponent", at = @At("RETURN"), cancellable = true)
	private void createStackComponent(CallbackInfoReturnable<ITextComponent> callbackInfoReturnable) {
		callbackInfoReturnable.setReturnValue(AsmHooks.createStackComponent((IFormattableTextComponent) callbackInfoReturnable.getReturnValue(), (ItemStack) (Object) this));
	}
}
