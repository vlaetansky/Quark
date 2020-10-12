package vazkii.quark.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.util.math.vector.Vector3d;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import vazkii.quark.base.handler.AsmHooks;

@Mixin(Entity.class)
public class EntityMixin {

	@Inject(method = "tick", at = @At("HEAD"))
	private void tick(CallbackInfo callbackInfo) {
		AsmHooks.updateChain((Entity) (Object) this);
	}

	@Inject(method = "func_230279_az_", at = @At("RETURN"), cancellable = true)
	private void isImmuneToFire(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		callbackInfoReturnable.setReturnValue(AsmHooks.getIsFireResistant(callbackInfoReturnable.getReturnValue(), (Entity) (Object) this));
	}

	@Inject(method = "move", at = @At("HEAD"))
	private void recordMotion(CallbackInfo callbackInfo) {
		AsmHooks.recordMotion((Entity) (Object) this);
	}

	@Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;doBlockCollisions()V", shift = At.Shift.AFTER), locals = LocalCapture.PRINT)
	private void applyCollisionLogic(MoverType typeIn, Vector3d pos, CallbackInfo callbackInfo, Vector3d vector3d) {
		AsmHooks.applyCollisionLogic((Entity) (Object) this, pos, vector3d);
	}
}
