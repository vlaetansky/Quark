package vazkii.quark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.quark.base.handler.QuarkPistonStructureResolver;
import vazkii.quark.content.automation.module.PistonsMoveTileEntitiesModule;

@Mixin(PistonBaseBlock.class)
public class PistonBaseBlockMixin {

	@Redirect(method = "isPushable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;hasBlockEntity()Z"))
	private static boolean isPushable(BlockState blockStateIn) {
		return PistonsMoveTileEntitiesModule.shouldMoveTE(blockStateIn.hasBlockEntity(), blockStateIn);
	}

	@Inject(method = "moveBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/piston/PistonStructureResolver;getToPush()Ljava/util/List;"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void moveBlocks(Level worldIn, BlockPos pos, Direction directionIn, boolean extending, CallbackInfoReturnable<Boolean> callbackInfoReturnable, BlockPos _pos, PistonStructureResolver pistonBlockStructureHelper) {
		PistonsMoveTileEntitiesModule.detachTileEntities(worldIn, pistonBlockStructureHelper, directionIn, extending);
	}

	@Redirect(method = {"checkIfExtend", "moveBlocks"}, at = @At(value = "NEW", target = "net/minecraft/world/level/block/piston/PistonStructureResolver"))
	private PistonStructureResolver transformStructureHelper(Level worldIn, BlockPos posIn, Direction pistonFacing, boolean extending) {
		return new QuarkPistonStructureResolver(new PistonStructureResolver(worldIn, posIn, pistonFacing, extending), worldIn, posIn, pistonFacing, extending);
	}
}
