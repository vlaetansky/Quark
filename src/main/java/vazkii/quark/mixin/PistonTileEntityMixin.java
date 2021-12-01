package vazkii.quark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import vazkii.quark.content.automation.module.PistonsMoveTileEntitiesModule;

@Mixin(PistonMovingBlockEntity.class)
public class PistonTileEntityMixin {

	@Redirect(method = {"tick", "clearPistonTileEntity"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private boolean setPistonBlock(Level world, BlockPos pos, BlockState newState, int flags) {
		return PistonsMoveTileEntitiesModule.setPistonBlock(world, pos, newState, flags);
	}
}
