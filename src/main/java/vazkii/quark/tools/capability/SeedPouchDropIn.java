package vazkii.quark.tools.capability;

import com.mojang.datafixers.util.Pair;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Tags;
import vazkii.arl.util.AbstractDropIn;
import vazkii.quark.tools.item.SeedPouchItem;

public class SeedPouchDropIn extends AbstractDropIn {

	@Override
	public boolean canDropItemIn(PlayerEntity player, ItemStack stack, ItemStack incoming) {
		Pair<ItemStack, Integer> contents = SeedPouchItem.getContents(stack);
		
		if(contents == null)
			return incoming.getItem().isIn(Tags.Items.SEEDS);
		
		return ItemStack.areItemsEqual(incoming, contents.getFirst());
	}

	@Override
	public ItemStack dropItemIn(PlayerEntity player, ItemStack stack, ItemStack incoming) {
		Pair<ItemStack, Integer> contents = SeedPouchItem.getContents(stack);

		if(contents == null)
			SeedPouchItem.setItemStack(stack, incoming);
		else SeedPouchItem.setCount(stack, contents.getSecond() + incoming.getCount());
		incoming.setCount(0);
		
		return stack;
	}

}
