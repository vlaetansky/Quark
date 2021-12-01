package vazkii.quark.content.management.capability;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.EmptyHandler;
import vazkii.arl.util.AbstractDropIn;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.handler.SimilarBlockTypeHandler;

public class ShulkerBoxDropIn extends AbstractDropIn {

	@Override
	public boolean canDropItemIn(Player player, ItemStack stack, ItemStack incoming, Slot slot) {
		return tryAddToShulkerBox(player, stack, incoming, slot, true) != null;
	}

	@Override
	public ItemStack dropItemIn(Player player, ItemStack stack, ItemStack incoming, Slot slot) {
		ItemStack ret = tryAddToShulkerBox(player, stack, incoming, slot, false); 
		return ret == null ? stack : ret;
	}

	private ItemStack tryAddToShulkerBox(Player player, ItemStack shulkerBox, ItemStack stack, Slot slot, boolean simulate) {
		if (!SimilarBlockTypeHandler.isShulkerBox(shulkerBox) || !slot.mayPickup(player))
			return null;

		CompoundTag cmp = ItemNBTHelper.getCompound(shulkerBox, "BlockEntityTag", false);
		if(cmp.contains("LootTable"))
			return null;
		
		if (cmp != null) {
			BlockEntity te = null;
			cmp = cmp.copy();	
			cmp.putString("id", "minecraft:shulker_box");				
			if (shulkerBox.getItem() instanceof BlockItem) {
				Block shulkerBoxBlock = Block.byItem(shulkerBox.getItem());
				BlockState defaultState = shulkerBoxBlock.defaultBlockState();
				if (shulkerBoxBlock.hasTileEntity(defaultState)) {
					te = shulkerBoxBlock.createTileEntity(defaultState, null);
					te.load(defaultState, cmp);
				}
			}

			if (te != null) {
				LazyOptional<IItemHandler> handlerHolder = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
				if (handlerHolder.isPresent()) {
					IItemHandler handler = handlerHolder.orElseGet(EmptyHandler::new);
					ItemStack result = ItemHandlerHelper.insertItem(handler, stack.copy(), simulate);
					boolean did = result.isEmpty() || result.getCount() != stack.getCount();

					if (did) {
						ItemStack copy = shulkerBox.copy();
						
						if(!simulate)
							stack.setCount(result.getCount());
						
						te.save(cmp);
						ItemNBTHelper.setCompound(copy, "BlockEntityTag", cmp);
						
						if(slot.mayPlace(copy))
							return copy;
					}
				}
			}
		}

		return null;
	}

}
