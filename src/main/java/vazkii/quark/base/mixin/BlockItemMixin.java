package vazkii.quark.base.mixin;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vazkii.quark.base.handler.AsmHooks;

@Mixin(BlockItem.class)
public class BlockItemMixin {

	@Shadow
	@Nullable
	protected native BlockState getStateForPlacement(BlockItemUseContext context);

	@Redirect(method = "tryPlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BlockItem;getStateForPlacement(Lnet/minecraft/item/BlockItemUseContext;)Lnet/minecraft/block/BlockState;"))
	private BlockState alterPlacementState(BlockItem self, BlockItemUseContext context) {
		return AsmHooks.alterPlacementState(getStateForPlacement(context), context);
	}
}
