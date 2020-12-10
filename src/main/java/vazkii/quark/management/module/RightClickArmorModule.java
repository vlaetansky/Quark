package vazkii.quark.management.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.network.QuarkNetwork;
import vazkii.quark.base.network.message.SwapArmorMessage;

@LoadModule(category = ModuleCategory.MANAGEMENT, hasSubscriptions = true)
public class RightClickArmorModule extends QuarkModule {

	private static boolean shouldCancelNextRelease = false;
	
	@SubscribeEvent 
	@OnlyIn(Dist.CLIENT)
	public void onRightClick(GuiScreenEvent.MouseClickedEvent.Pre event) {
		Minecraft mc = Minecraft.getInstance();
		Screen gui = mc.currentScreen;
		if(gui instanceof ContainerScreen && event.getButton() == 1) {
			ContainerScreen<?> container = (ContainerScreen<?>) gui;
			Slot under = container.getSlotUnderMouse();
			if(under != null) {
				ItemStack held = mc.player.inventory.getItemStack();

				if(held.isEmpty() && swap(mc.player, under.slotNumber)) {
					QuarkNetwork.sendToServer(new SwapArmorMessage(under.slotNumber));
					
					shouldCancelNextRelease = true;
					event.setCanceled(true);
				}
			}
		}
	}
	
	public static boolean swap(PlayerEntity player, int slot) {
		Slot slotUnder = player.openContainer.getSlot(slot);
		ItemStack stack = slotUnder.getStack();
		
		if(stack.getItem() instanceof ArmorItem && slotUnder.canTakeStack(player)) {
			ArmorItem armor = (ArmorItem) stack.getItem();
			
			EquipmentSlotType equipSlot = armor.getEquipmentSlot();
			ItemStack currArmor = player.getItemStackFromSlot(equipSlot);
			
			if(currArmor.isEmpty() || (EnchantmentHelper.getEnchantmentLevel(Enchantments.BINDING_CURSE, currArmor) == 0 && currArmor != stack)) {
				player.setItemStackToSlot(equipSlot, stack.copy());
				
				player.inventory.setInventorySlotContents(slotUnder.getSlotIndex(), currArmor.copy());
				return true;
			}
		}
		
		return false;
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	@OnlyIn(Dist.CLIENT)
	public void onRightClickRelease(GuiScreenEvent.MouseReleasedEvent.Pre event) {
		if(shouldCancelNextRelease) {
			shouldCancelNextRelease = false;
			event.setCanceled(true);
		}
	}

	
}
