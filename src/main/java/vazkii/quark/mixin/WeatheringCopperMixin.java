package vazkii.quark.mixin;

import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.quark.base.block.CustomWeatheringCopper;

import java.util.Optional;

@Mixin(WeatheringCopper.class)
public interface WeatheringCopperMixin {

	@Inject(method = "getPrevious(Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/Optional;", at = @At("HEAD"), cancellable = true)
	private static void getPrevious(BlockState state, CallbackInfoReturnable<Optional<BlockState>> cir) {
		if (state.getBlock() instanceof CustomWeatheringCopper copper)
			cir.setReturnValue(copper.getPrevious(state));
	}

	@Inject(method = "getFirst(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/block/state/BlockState;", at = @At("HEAD"), cancellable = true)
	private static void getFirst(BlockState state, CallbackInfoReturnable<BlockState> cir) {
		if (state.getBlock() instanceof CustomWeatheringCopper copper)
			cir.setReturnValue(copper.getFirst(state));
	}

}
