package vazkii.quark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import vazkii.quark.content.management.module.ExpandedItemInteractionsModule;

@Mixin(Item.class)
public class ItemMixin {
	
	@Inject(method = "overrideStackedOnOther", at = @At("RETURN"), cancellable = true)
	public void overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		if(!callbackInfoReturnable.getReturnValueZ())
			callbackInfoReturnable.setReturnValue(ExpandedItemInteractionsModule.overrideStackedOnOther(stack, slot, action, player));	
	}
	
	@Inject(method = "overrideOtherStackedOnMe", at = @At("RETURN"), cancellable = true)
	public void overrideOtherStackedOnMe(ItemStack stack, ItemStack incoming, Slot slot, ClickAction action, Player player, SlotAccess accessor, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		if(!callbackInfoReturnable.getReturnValueZ())
			callbackInfoReturnable.setReturnValue(ExpandedItemInteractionsModule.overrideOtherStackedOnMe(stack, incoming, slot, action, player, accessor));
	}

	
}
