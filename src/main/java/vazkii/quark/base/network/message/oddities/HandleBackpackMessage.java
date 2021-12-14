package vazkii.quark.base.network.message.oddities;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import vazkii.arl.network.IMessage;
import vazkii.quark.addons.oddities.container.BackpackContainer;

public class HandleBackpackMessage implements IMessage {

	private static final long serialVersionUID = 3474816381329541425L;

	public boolean open;

	public HandleBackpackMessage() { }

	public HandleBackpackMessage(boolean open) { 
		this.open = open;
	}

	@Override
	public boolean receive(NetworkEvent.Context context) {
		ServerPlayer player = context.getSender();
		context.enqueueWork(() -> {
			if(open) {
				ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
				if(stack.getItem() instanceof MenuProvider) {
//					Inventory inventory = player.getInventory();
//					ItemStack holding = inventory.getCarried(); TODO FIX how do you get this now?
//					inventory.setCarried(ItemStack.EMPTY);
//					NetworkHooks.openGui(player, (MenuProvider) stack.getItem(), player.blockPosition());
//					inventory.setCarried(holding);
				}
			} else {
				BackpackContainer.saveCraftingInventory(player);
				player.containerMenu = player.inventoryMenu;
			}
		});

		return true;
	}

}
