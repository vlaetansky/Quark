package vazkii.quark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import vazkii.quark.content.building.module.VariantLaddersModule;

@Mixin(TrapDoorBlock.class)
public class TrapDoorBlockMixin {

	@Inject(method = "isLadder", at = @At("RETURN"), cancellable = true, remap = false)
	private void isTrapdoorLadder(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		callbackInfoReturnable.setReturnValue(VariantLaddersModule.isTrapdoorLadder(callbackInfoReturnable.getReturnValue(), world, pos));
	}
}
