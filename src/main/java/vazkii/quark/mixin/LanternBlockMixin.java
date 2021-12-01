package vazkii.quark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Lantern;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import vazkii.quark.content.building.module.WoodenPostsModule;

@Mixin(Lantern.class)
public class LanternBlockMixin {

	@Inject(method = "isValidPosition", at = @At("RETURN"), cancellable = true)
	private void isValidPosition(BlockState state, LevelReader worldIn, BlockPos pos, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		callbackInfoReturnable.setReturnValue(WoodenPostsModule.canLanternConnect(state, worldIn, pos, callbackInfoReturnable.getReturnValue()));
	}
	
}
