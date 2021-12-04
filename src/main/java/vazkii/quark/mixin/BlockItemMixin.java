package vazkii.quark.mixin;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.quark.content.tweaks.module.LockRotationModule;

@Mixin(BlockItem.class)
public class BlockItemMixin {

	@Shadow
	@Nullable
	protected native BlockState getPlacementState(BlockPlaceContext context);

	@Redirect(method = "place", at = @At(value = "INVOKE", 
			target = "Lnet/minecraft/world/item/BlockItem;getPlacementState(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/level/block/state/BlockState;"))
	private BlockState alterPlacementState(BlockItem self, BlockPlaceContext context) {
		return LockRotationModule.fixBlockRotation(getPlacementState(context), context);
	}
}
