//package vazkii.quark.content.management.module;
//
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.screens.Screen;
//import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
//import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
//import net.minecraft.world.entity.EquipmentSlot;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.inventory.Slot;
//import net.minecraft.world.item.ArmorItem;
//import net.minecraft.world.item.ElytraItem;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.enchantment.EnchantmentHelper;
//import net.minecraft.world.item.enchantment.Enchantments;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//import net.minecraftforge.client.event.GuiScreenEvent;
//import net.minecraftforge.eventbus.api.EventPriority;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import vazkii.quark.base.module.LoadModule;
//import vazkii.quark.base.module.ModuleCategory;
//import vazkii.quark.base.module.ModuleLoader;
//import vazkii.quark.base.module.QuarkModule;
//import vazkii.quark.base.network.QuarkNetwork;
//import vazkii.quark.base.network.message.SwapArmorMessage;
//
//@LoadModule(category = ModuleCategory.MANAGEMENT, hasSubscriptions = true)
//public class RightClickArmorModule extends QuarkModule { TODO rewrite
//
//	private static boolean shouldCancelNextRelease = false;
//	
//	@SubscribeEvent 
//	@OnlyIn(Dist.CLIENT)
//	public void onRightClick(GuiScreenEvent.MouseClickedEvent.Pre event) {
//		Minecraft mc = Minecraft.getInstance();
//		Screen gui = mc.screen;
//		if(gui instanceof AbstractContainerScreen && !(gui instanceof CreativeModeInventoryScreen) && event.getButton() == 1 && !Screen.hasShiftDown()) {
//			AbstractContainerScreen<?> container = (AbstractContainerScreen<?>) gui;
//			Slot under = container.getSlotUnderMouse();
//			if(under != null) {
//				ItemStack held = mc.player.inventory.getCarried();
//
//				if(held.isEmpty() && swap(mc.player, under.index)) {
//					QuarkNetwork.sendToServer(new SwapArmorMessage(under.index));
//					
//					shouldCancelNextRelease = true;
//					event.setCanceled(true);
//				}
//			}
//		}
//	}
//	
//	public static boolean swap(Player player, int slot) {
//		if(!ModuleLoader.INSTANCE.isModuleEnabled(RightClickArmorModule.class))
//			return false;
//		
//		Slot slotUnder = player.containerMenu.getSlot(slot);
//		ItemStack stack = slotUnder.getItem();
//		
//		EquipmentSlot equipSlot = null;
//		
//		if(stack.getItem() instanceof ArmorItem) {
//			ArmorItem armor = (ArmorItem) stack.getItem();
//			equipSlot = armor.getSlot();
//		} else if(stack.getItem() instanceof ElytraItem)
//			equipSlot = EquipmentSlot.CHEST;
//		
//		if(equipSlot != null) {
//			ItemStack currArmor = player.getItemBySlot(equipSlot);
//			
//			if(slotUnder.mayPickup(player) && slotUnder.mayPlace(currArmor)) 
//				if(currArmor.isEmpty() || (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BINDING_CURSE, currArmor) == 0 && currArmor != stack)) {
//					int index = slotUnder.getSlotIndex();
//					if(index < slotUnder.container.getContainerSize()) {
//						player.setItemSlot(equipSlot, stack.copy());
//						
//						slotUnder.container.setItem(index, currArmor.copy());
//						slotUnder.onQuickCraft(stack, currArmor);
//						return true;
//					}
//				}
//		}
//		
//		return false;
//	}
//
//	@SubscribeEvent(priority = EventPriority.HIGHEST)
//	@OnlyIn(Dist.CLIENT)
//	public void onRightClickRelease(GuiScreenEvent.MouseReleasedEvent.Pre event) {
//		if(shouldCancelNextRelease) {
//			shouldCancelNextRelease = false;
//			event.setCanceled(true);
//		}
//	}
//	
//}
