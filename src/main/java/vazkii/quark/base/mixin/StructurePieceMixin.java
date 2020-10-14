package vazkii.quark.base.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import vazkii.quark.base.handler.AsmHooks;

@Mixin(StructurePiece.class)
public class StructurePieceMixin {

	@ModifyVariable(method = "correctFacing", at = @At("HEAD"), index = 2)
	private static BlockState correctFacing(BlockState blockStateIn) {
		return AsmHooks.getGenerationChestBlockState(blockStateIn);
	}
	
}
