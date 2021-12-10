package vazkii.quark.content.management.module;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.EmptyHandler;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.handler.SimilarBlockTypeHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = ModuleCategory.MANAGEMENT)
public class ExpandedItemInteractionsModule extends QuarkModule {

	@Config public static boolean enableArmorInteraction = true;
	@Config public static boolean enableShulkerBoxInteraction = true;
	@Config public static boolean enableLavaInteraction = true;

	private static boolean staticEnabled = false;

	@Override
	public void configChanged() {
		staticEnabled = configEnabled;
	}
	
	public static boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
		if(!staticEnabled || action == ClickAction.PRIMARY)
			return false;

		// None yet but may be used later idk
		
		return false;
	}

	public static boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack incoming, Slot slot, ClickAction action, Player player, SlotAccess accessor) {
		if(!staticEnabled || action == ClickAction.PRIMARY)
			return false;
		
		if(enableLavaInteraction && lavaBucketOverride(stack, incoming, slot, action, player, accessor))
			return true;

		if(enableArmorInteraction && armorOverride(stack, incoming, slot, action, player, accessor))
			return true;
		
		if(enableShulkerBoxInteraction && shulkerOverride(stack, incoming, slot, action, player, accessor))
			return true;

		return false;
	}

	private static boolean armorOverride(ItemStack stack, ItemStack incoming, Slot slot, ClickAction action, Player player, SlotAccess accessor) {
		if(incoming.isEmpty()) {
			EquipmentSlot equipSlot = null;

			if(stack.getItem() instanceof ArmorItem) {
				ArmorItem armor = (ArmorItem) stack.getItem();
				equipSlot = armor.getSlot();
			} else if(stack.getItem() instanceof ElytraItem)
				equipSlot = EquipmentSlot.CHEST;

			if(equipSlot != null) {
				ItemStack currArmor = player.getItemBySlot(equipSlot);

				if(slot.mayPickup(player) && slot.mayPlace(currArmor)) 
					if(currArmor.isEmpty() || (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BINDING_CURSE, currArmor) == 0 && currArmor != stack)) {
						int index = slot.getSlotIndex();
						if(index < slot.container.getContainerSize()) {
							player.setItemSlot(equipSlot, stack.copy());

							slot.container.setItem(index, currArmor.copy());
							slot.onQuickCraft(stack, currArmor);
							return true;
						}
					}
			}
		}

		return false;
	}

	public static boolean lavaBucketOverride(ItemStack stack, ItemStack incoming, Slot slot, ClickAction action, Player player, SlotAccess accessor) {
		if(stack.getItem() == Items.LAVA_BUCKET 
				&& !incoming.isEmpty() 
				&& !player.isCreative() 
				&& slot.allowModification(player) 
				&& slot.mayPlace(stack) 
				&& !incoming.getItem().isFireResistant() 
				&& !SimilarBlockTypeHandler.isShulkerBox(incoming)) {

			incoming.setCount(0);
			if(!player.level.isClientSide)
				player.level.playSound(null, player.blockPosition(), SoundEvents.LAVA_EXTINGUISH, SoundSource.PLAYERS, 0.25F, 2F + (float) Math.random());

			return true;
		}

		return false;
	}

	private static boolean shulkerOverride(ItemStack stack, ItemStack incoming, Slot slot, ClickAction action, Player player, SlotAccess accessor) {
		if(!incoming.isEmpty() && tryAddToShulkerBox(player, stack, incoming, slot, true) != null) {
			ItemStack finished = tryAddToShulkerBox(player, stack, incoming, slot, false);
			
			if(finished != null) {
				slot.set(finished);
				return true;
			}
		}
		
		return false;
	}

	private static ItemStack tryAddToShulkerBox(Player player, ItemStack shulkerBox, ItemStack stack, Slot slot, boolean simulate) {
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
				if (shulkerBoxBlock instanceof EntityBlock) {
					te = ((EntityBlock) shulkerBoxBlock).newBlockEntity(BlockPos.ZERO, defaultState);
					te.load(cmp);
				}
			}

			if (te != null) {
				LazyOptional<IItemHandler> handlerHolder = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
				if (handlerHolder.isPresent()) {
					IItemHandler handler = handlerHolder.orElseGet(EmptyHandler::new);
					ItemStack result = ItemHandlerHelper.insertItem(handler, stack.copy(), simulate);
					boolean did = result.isEmpty() || result.getCount() != stack.getCount();

					if (did) {
						ItemStack workStack = shulkerBox.copy();
						if(!simulate)
							stack.setCount(result.getCount());
						
						cmp = te.saveWithFullMetadata();
						ItemNBTHelper.setCompound(workStack, "BlockEntityTag", cmp);
						
						if(slot.mayPlace(workStack))
							return workStack;
					}
				}
			}
		}

		return null;
	}
	
}
