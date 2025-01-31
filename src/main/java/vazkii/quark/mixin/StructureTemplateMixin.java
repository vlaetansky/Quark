package vazkii.quark.mixin;

import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import vazkii.quark.base.handler.StructureBlockReplacementHandler;

@Mixin(StructureTemplate.class)
public class StructureTemplateMixin {

	@ModifyVariable(method = "placeInWorld(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;Ljava/util/Random;I)Z",
			at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate$StructureBlockInfo;nbt:Lnet/minecraft/nbt/CompoundTag;", ordinal = 0),
			index = 22)
	private BlockState captureLocalBlockstate(BlockState state, ServerLevelAccessor accessor) {
		return StructureBlockReplacementHandler.getResultingBlockState(accessor, state);
	}
}
