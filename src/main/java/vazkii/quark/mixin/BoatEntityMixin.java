package vazkii.quark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import vazkii.quark.content.experimental.module.GameNerfsModule;
import vazkii.quark.content.management.entity.ChestPassengerEntity;

@Mixin(Boat.class)
public class BoatEntityMixin {

	@Inject(method = "getControllingPassenger", at = @At("RETURN"), cancellable = true)
	private void ensurePassengerIsNotChest(CallbackInfoReturnable<Entity> callbackInfoReturnable) {
		callbackInfoReturnable.setReturnValue(ensurePassengerIsNotChest(callbackInfoReturnable.getReturnValue()));
	}
	
	@Inject(method = "getBoatGlide", at = @At("RETURN"), cancellable = true)
	private void getBoatGlide(CallbackInfoReturnable<Float> callbackInfoReturnable) {
		callbackInfoReturnable.setReturnValue(GameNerfsModule.getBoatGlide(callbackInfoReturnable.getReturnValueF()));
	}
	
	private static Entity ensurePassengerIsNotChest(Entity passenger) {
		if (passenger instanceof ChestPassengerEntity)
			return null;
		return passenger;
	}
}
