package vazkii.quark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import vazkii.quark.base.handler.QuarkPistonStructureHelper;
import vazkii.quark.content.automation.module.PistonsMoveTileEntitiesModule;

@Mixin(PistonBaseBlock.class)
public class PistonBlockMixin {

	@Redirect(method = "canPush", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;hasTileEntity()Z", remap = false /* bc hasTileEntity is a forge method */))
	private static boolean hasTileEntity(BlockState blockStateIn) {
		return PistonsMoveTileEntitiesModule.shouldMoveTE(blockStateIn.hasTileEntity(), blockStateIn);
	}

	@Inject(method = "doMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PistonBlockStructureHelper;getBlocksToMove()Ljava/util/List;"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void postPistonPush(Level worldIn, BlockPos pos, Direction directionIn, boolean extending, CallbackInfoReturnable<Boolean> callbackInfoReturnable, BlockPos _pos, PistonStructureResolver pistonBlockStructureHelper) {
		PistonsMoveTileEntitiesModule.detachTileEntities(worldIn, pistonBlockStructureHelper, directionIn, extending);
	}

	@Redirect(method = {"checkForMove", "doMove"}, at = @At(value = "NEW", target = "net/minecraft/block/PistonBlockStructureHelper"))
	private PistonStructureResolver transformStructureHelper(Level worldIn, BlockPos posIn, Direction pistonFacing, boolean extending) {
		return new QuarkPistonStructureHelper(new PistonStructureResolver(worldIn, posIn, pistonFacing, extending), worldIn, posIn, pistonFacing, extending);
	}
}
