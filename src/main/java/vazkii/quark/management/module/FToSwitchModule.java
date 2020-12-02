package vazkii.quark.management.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardKeyPressedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.network.QuarkNetwork;
import vazkii.quark.base.network.message.SwapItemsMessage;

@LoadModule(category = ModuleCategory.MANAGEMENT, hasSubscriptions = true, subscribeOn = Dist.CLIENT)
public class FToSwitchModule extends QuarkModule {

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	@SuppressWarnings("rawtypes")
	public void keyboardEvent(KeyboardKeyPressedEvent event) {
		Minecraft mc = Minecraft.getInstance();
		
		if(event.getKeyCode() == mc.gameSettings.keyBindSwapHands.getKey().getKeyCode() && event.getGui() instanceof ContainerScreen) {
			ContainerScreen gui = (ContainerScreen) event.getGui();
			Slot slot = gui.getSlotUnderMouse();
			if(slot != null && slot.canTakeStack(mc.player)) {
				IInventory inv = slot.inventory;
				if(inv instanceof PlayerInventory) {
					int index = slot.getSlotIndex();

					if(index < ((PlayerInventory) inv).mainInventory.size()) {
						QuarkNetwork.sendToServer(new SwapItemsMessage(index));
						event.setCanceled(true);
					}
				}
			}
		}
	}

	public static void switchItems(PlayerEntity player, int slot) {
		if(!ModuleLoader.INSTANCE.isModuleEnabled(FToSwitchModule.class) || slot >= player.inventory.mainInventory.size())
			return;

		int offHandSlot = player.inventory.getSizeInventory() - 1;
		ItemStack stackAtSlot = player.inventory.getStackInSlot(slot);
		ItemStack stackAtOffhand = player.inventory.getStackInSlot(offHandSlot);

		player.inventory.setInventorySlotContents(slot, stackAtOffhand);
		player.inventory.setInventorySlotContents(offHandSlot, stackAtSlot);
	}
	
}
