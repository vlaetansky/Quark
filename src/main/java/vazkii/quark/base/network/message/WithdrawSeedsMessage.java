package vazkii.quark.base.network.message;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import vazkii.arl.network.IMessage;
import vazkii.quark.content.tools.item.SeedPouchItem;
import vazkii.quark.content.tools.module.SeedPouchModule;

public class WithdrawSeedsMessage implements IMessage {

	private static final long serialVersionUID = 6823238786661829916L;
	
	public int slot;
	
	public WithdrawSeedsMessage() { }
	
	public WithdrawSeedsMessage(int slot) {
		this.slot = slot;
	}
	
	@Override
	public boolean receive(NetworkEvent.Context context) {
		context.enqueueWork(() -> {
			Player player = context.getSender();
			ItemStack pouch = player.containerMenu.getSlot(slot).getItem();
			ItemStack held = player.inventory.getCarried();

			if(pouch.getItem() == SeedPouchModule.seed_pouch) {
    			Pair<ItemStack, Integer> contents = SeedPouchItem.getContents(pouch);
    			if(contents != null) {
    				ItemStack seed = contents.getLeft();
    				int pouchCount = contents.getRight();
    				if(held.isEmpty()) {
    					int takeOut = Math.min(seed.getMaxStackSize(), contents.getRight());
    					
    					ItemStack result = seed.copy();
    					result.setCount(takeOut);
    					player.inventory.setCarried(result);

    					SeedPouchItem.setCount(pouch, pouchCount - takeOut);
    				}
    			}
			}
		});
		
		return false;
	}

}
