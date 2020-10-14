package vazkii.quark.base.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.template.Template;
import vazkii.quark.base.handler.AsmHooks;

@Mixin(Template.class)
public class TemplateMixin {

	//   public boolean func_237146_a_(IServerWorld p_237146_1_, BlockPos p_237146_2_, BlockPos p_237146_3_, PlacementSettings p_237146_4_, Random p_237146_5_, int p_237146_6_) {
	
	@ModifyArg(method = "func_237146_a_(Lnet/minecraft/world/IServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/feature/template/PlacementSettings;Ljava/util/Random;I)Z", 
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/IServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"),
			index = 1)
	private BlockState injectOnSetBlockState(BlockState state) {
		return AsmHooks.getGenerationChestBlockState(state);
	}


}
