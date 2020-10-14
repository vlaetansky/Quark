package vazkii.quark.base.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.PistonTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vazkii.quark.base.handler.AsmHooks;

@Mixin(PistonTileEntity.class)
public class PistonTileEntityMixin {

	@Redirect(method = {"tick", "clearPistonTileEntity"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private boolean setPistonBlock(World world, BlockPos pos, BlockState newState, int flags) {
		return AsmHooks.setPistonBlock(world, pos, newState, flags);
	}
}
