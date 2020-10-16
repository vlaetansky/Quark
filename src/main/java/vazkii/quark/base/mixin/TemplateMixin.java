package vazkii.quark.base.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.feature.template.Template;
import vazkii.quark.base.handler.AsmHooks;

@Mixin(Template.class)
public class TemplateMixin {

	@ModifyVariable(method = "func_237146_a_(Lnet/minecraft/world/IServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/feature/template/PlacementSettings;Ljava/util/Random;I)Z", 
			at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/block/AbstractBlock$AbstractBlockState;rotate(Lnet/minecraft/util/Rotation;)Lnet/minecraft/block/BlockState;"),
			index = 8) // blockstate (could be 14?)
	private BlockState captureLocalBlockstate(BlockState state) {
		return AsmHooks.getGenerationChestBlockState(state);
	}
	

}
