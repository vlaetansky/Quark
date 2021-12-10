package vazkii.quark.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.item.Items;

@Mixin(Items.class)
public class ItemsMixin {
	
	@Inject(method = "ifPart2", at = @At("RETURN"), cancellable = true)
	private static <T> void overrideStackedOnOther(T val, CallbackInfoReturnable<Optional<T>> callbackInfoReturnable) {
		callbackInfoReturnable.setReturnValue(Optional.of(val));	
	}

}
