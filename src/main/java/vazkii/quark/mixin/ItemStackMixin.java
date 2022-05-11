package vazkii.quark.mixin;

import net.minecraft.world.item.Rarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import vazkii.quark.content.experimental.module.EmptyNBTBegoneModule;
import vazkii.quark.content.management.module.ItemSharingModule;
import vazkii.quark.content.tools.module.AncientTomesModule;

@Mixin(ItemStack.class)
public class ItemStackMixin {

	@Inject(method = "tagMatches", at = @At("HEAD"), cancellable = true)
	private static void forceTagMatch(ItemStack stack1, ItemStack stack2, CallbackInfoReturnable<Boolean> cir) {
		if (EmptyNBTBegoneModule.areTagsSameIgnoringEmptyTag(stack1, stack2))
			cir.setReturnValue(true);
	}

	@Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
	private void getHoverName(CallbackInfoReturnable<Component> callbackInfoReturnable) {
		callbackInfoReturnable.setReturnValue(ItemSharingModule.createStackComponent((ItemStack) (Object) this, (MutableComponent) callbackInfoReturnable.getReturnValue()));
	}

	@Inject(method = "getRarity", at = @At("RETURN"), cancellable = true)
	private void getRarity(CallbackInfoReturnable<Rarity> callbackInfoReturnable) {
		callbackInfoReturnable.setReturnValue(AncientTomesModule.shiftRarity((ItemStack) (Object) this, callbackInfoReturnable.getReturnValue()));
	}
}
