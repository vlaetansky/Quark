package vazkii.quark.base.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonBlockStructureHelper;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import vazkii.quark.base.handler.AsmHooks;

@Mixin(PistonBlock.class)
public class PistonBlockMixin {

	@Redirect(method = "canPush", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;hasTileEntity()Z"))
	private static boolean hasTileEntity(BlockState blockStateIn) {
		return AsmHooks.shouldPistonMoveTE(blockStateIn.hasTileEntity(), blockStateIn);
	}

	@Inject(method = "doMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PistonBlockStructureHelper;getBlocksToMove()Ljava/util/List;"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void postPistonPush(World worldIn, BlockPos pos, Direction directionIn, boolean extending, CallbackInfoReturnable<Boolean> callbackInfoReturnable, BlockPos _pos, PistonBlockStructureHelper pistonBlockStructureHelper) {
		AsmHooks.postPistonPush(pistonBlockStructureHelper, worldIn, directionIn, extending);
	}

	@Redirect(method = {"checkForMove", "doMove"}, at = @At(value = "NEW", target = "net/minecraft/block/PistonBlockStructureHelper"))
	private PistonBlockStructureHelper transformStructureHelper(World worldIn, BlockPos posIn, Direction pistonFacing, boolean extending) {
		return AsmHooks.transformStructureHelper(new PistonBlockStructureHelper(worldIn, posIn, pistonFacing, extending), worldIn, posIn, pistonFacing, extending);
	}
}
