package vazkii.quark.base.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.BoatEntity;
import vazkii.quark.automation.module.ChainLinkageModule;
import vazkii.quark.management.entity.ChestPassengerEntity;

@Mixin(BoatEntity.class)
public class BoatEntityMixin {

	@Inject(method = "getControllingPassenger", at = @At("RETURN"), cancellable = true)
	private void ensurePassengerIsNotChest(CallbackInfoReturnable<Entity> callbackInfoReturnable) {
		callbackInfoReturnable.setReturnValue(ensurePassengerIsNotChest(callbackInfoReturnable.getReturnValue()));
	}

	@Inject(method = "attackEntityFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/item/BoatEntity;remove()V"))
	private void attackEntityFrom$dropChain(CallbackInfoReturnable<Boolean> callbackInfo) {
		ChainLinkageModule.drop((BoatEntity) (Object) this);
	}

	@Inject(method = "updateFallState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/item/BoatEntity;remove()V"))
	private void updateFallState$dropChain(CallbackInfo callbackInfo) {
		ChainLinkageModule.drop((BoatEntity) (Object) this);
	}
	
	private static Entity ensurePassengerIsNotChest(Entity passenger) {
		if (passenger instanceof ChestPassengerEntity)
			return null;
		return passenger;
	}
}
