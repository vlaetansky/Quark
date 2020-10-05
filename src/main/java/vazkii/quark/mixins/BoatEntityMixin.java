package vazkii.quark.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.BoatEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.quark.base.handler.AsmHooks;

@Mixin(BoatEntity.class)
public class BoatEntityMixin {

	@Inject(method = "getControllingPassenger", at = @At("RETURN"), cancellable = true)
	private void ensurePassengerIsNotChest(CallbackInfoReturnable<Entity> callbackInfoReturnable) {
		callbackInfoReturnable.setReturnValue(AsmHooks.ensurePassengerIsNotChest(callbackInfoReturnable.getReturnValue()));
	}

	@Inject(method = "attackEntityFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/item/BoatEntity;remove()V"))
	private void onRemove0(CallbackInfoReturnable<Boolean> callbackInfo) {
		AsmHooks.dropChain((BoatEntity) (Object) this);
	}

	@Inject(method = "updateFallState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/item/BoatEntity;remove()V"))
	private void onRemove1(CallbackInfo callbackInfo) {
		AsmHooks.dropChain((BoatEntity) (Object) this);
	}
}
