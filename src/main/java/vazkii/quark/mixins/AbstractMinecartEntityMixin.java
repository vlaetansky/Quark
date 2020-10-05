package vazkii.quark.mixins;

import net.minecraft.entity.item.minecart.AbstractMinecartEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.quark.base.handler.AsmHooks;

@Mixin(AbstractMinecartEntity.class)
public class AbstractMinecartEntityMixin {

	@Inject(method = "tick", at = @At("HEAD"))
	private void updateChain(CallbackInfo callbackInfo) {
		AsmHooks.updateChain((AbstractMinecartEntity) (Object) this);
	}

	@Inject(method = "attackEntityFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/item/minecart/AbstractMinecartEntity;remove()V"))
	private void recordMotion0(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		AsmHooks.recordMotion((AbstractMinecartEntity) (Object) this);
	}

	@Inject(method = "killMinecart", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/item/minecart/AbstractMinecartEntity;remove()V"))
	private void recordMotion1(CallbackInfo callbackInfo) {
		AsmHooks.recordMotion((AbstractMinecartEntity) (Object) this);
	}
}
