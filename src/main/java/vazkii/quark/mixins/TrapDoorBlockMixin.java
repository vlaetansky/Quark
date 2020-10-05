package vazkii.quark.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.quark.base.handler.AsmHooks;

@Mixin(TrapDoorBlock.class)
public class TrapDoorBlockMixin {

	@Inject(method = "isLadder", at = @At("RETURN"), cancellable = true, remap = false)
	private void isLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		callbackInfoReturnable.setReturnValue(AsmHooks.isTrapdoorLadder(callbackInfoReturnable.getReturnValue(), world, pos));
	}
}
