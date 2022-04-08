package vazkii.quark.mixin.client;

import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import vazkii.quark.content.client.module.UsesForCursesModule;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Gui.class)
public class GuiMixin {

	@Redirect(method = "render",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;getArmor(I)Lnet/minecraft/world/item/ItemStack;"))
	public ItemStack changeArmorItem(Inventory instance, int idx) {
		ItemStack stackInInventory = instance.getItem(idx);
		if (UsesForCursesModule.shouldHidePumpkinOverlay(stackInInventory))
			return ItemStack.EMPTY;
		return stackInInventory;
	}
}
