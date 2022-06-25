package vazkii.quark.content.management.module;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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

import java.util.List;

@LoadModule(category = ModuleCategory.MANAGEMENT, hasSubscriptions = true, subscribeOn = Dist.CLIENT)
public class ExpandedItemInteractionsModule extends QuarkModule {

	@Config
	public static boolean enableArmorInteraction = true;
	@Config
	public static boolean enableShulkerBoxInteraction = true;
	@Config
	public static boolean enableLavaInteraction = true;

	private static boolean staticEnabled = false;

	@Override
	public void configChanged() {
		staticEnabled = configEnabled;
	}

	public static boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
		if (!staticEnabled || action == ClickAction.PRIMARY)
			return false;

		ItemStack stackAt = slot.getItem();
		if (enableShulkerBoxInteraction && shulkerOverride(stack, stackAt, slot, action, player, false, false)) {
			if (player.containerMenu != null)
				player.containerMenu.slotsChanged(slot.container);
			return true;
		}

		return false;
	}

	public static boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack incoming, Slot slot, ClickAction action, Player player, SlotAccess accessor) {
		if (!staticEnabled || action == ClickAction.PRIMARY)
			return false;

		if (enableLavaInteraction && lavaBucketOverride(stack, incoming, slot, action, player))
			return true;

		if (enableArmorInteraction && armorOverride(stack, incoming, slot, action, player, false))
			return true;

		return enableShulkerBoxInteraction && shulkerOverride(stack, incoming, slot, action, player, true, true);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onDrawScreen(ScreenEvent.DrawScreenEvent.Post event) {
		Minecraft mc = Minecraft.getInstance();
		Screen gui = mc.screen;
		if (mc.player != null && gui instanceof AbstractContainerScreen<?> containerGui) {
			ItemStack held = containerGui.getMenu().getCarried();
			if (!held.isEmpty()) {
				Slot under = containerGui.getSlotUnderMouse();

				if (under != null) {
					ItemStack underStack = under.getItem();

					int x = event.getMouseX();
					int y = event.getMouseY();
					if (enableLavaInteraction && canTrashItem(underStack, held, under, mc.player)) {
						gui.renderComponentTooltip(event.getPoseStack(), List.of(new TranslatableComponent("quark.misc.trash_item").withStyle(ChatFormatting.RED)), x, y);
					} else if (enableShulkerBoxInteraction && tryAddToShulkerBox(mc.player, underStack, held, under, true, true, true) != null) {
						gui.renderComponentTooltip(event.getPoseStack(), List.of(new TranslatableComponent(
							 SimilarBlockTypeHandler.isShulkerBox(held) ? "quark.misc.merge_shulker_box" : "quark.misc.insert_shulker_box"
						).withStyle(ChatFormatting.YELLOW)), x, y, underStack);
					} else if (enableShulkerBoxInteraction && SimilarBlockTypeHandler.isShulkerBox(underStack)) {
						gui.renderComponentTooltip(event.getPoseStack(), gui.getTooltipFromItem(underStack), x, y, underStack);
					}
				}

			}
		}
	}


	private static boolean armorOverride(ItemStack stack, ItemStack incoming, Slot slot, ClickAction action, Player player, boolean simulate) {
		if (incoming.isEmpty()) {
			EquipmentSlot equipSlot = null;

			if (stack.getItem() instanceof ArmorItem armor) {
				equipSlot = armor.getSlot();
			} else if (stack.getItem() instanceof ElytraItem)
				equipSlot = EquipmentSlot.CHEST;

			if (equipSlot != null) {
				ItemStack currArmor = player.getItemBySlot(equipSlot);

				if (slot.mayPickup(player) && slot.mayPlace(currArmor))
					if (currArmor.isEmpty() || (!EnchantmentHelper.hasBindingCurse(currArmor) && currArmor != stack)) {
						int index = slot.getSlotIndex();
						if (index < slot.container.getContainerSize()) {
							if (!simulate) {
								player.setItemSlot(equipSlot, stack.copy());

								slot.container.setItem(index, currArmor.copy());
								slot.onQuickCraft(stack, currArmor);
							}
							return true;
						}
					}
			}
		}

		return false;
	}

	private static boolean canTrashItem(ItemStack stack, ItemStack incoming, Slot slot, Player player) {
		return stack.getItem() == Items.LAVA_BUCKET
				&& !incoming.isEmpty()
				&& !player.getAbilities().instabuild
				&& slot.allowModification(player)
				&& slot.mayPlace(stack)
				&& !incoming.getItem().isFireResistant()
				&& !SimilarBlockTypeHandler.isShulkerBox(incoming);
	}

	public static boolean lavaBucketOverride(ItemStack stack, ItemStack incoming, Slot slot, ClickAction action, Player player) {
		if (canTrashItem(stack, incoming, slot, player)) {

			incoming.setCount(0);
			if (!player.level.isClientSide)
				player.level.playSound(null, player.blockPosition(), SoundEvents.LAVA_EXTINGUISH, SoundSource.PLAYERS, 0.25F, 2F + (float) Math.random());

			return true;
		}

		return false;
	}

	private static boolean shulkerOverride(ItemStack stack, ItemStack incoming, Slot slot, ClickAction action, Player player, boolean setSlot, boolean allowDump) {
		if (!incoming.isEmpty() && tryAddToShulkerBox(player, stack, incoming, slot, true, true, allowDump) != null) {
			ItemStack finished = tryAddToShulkerBox(player, stack, incoming, slot, false, setSlot, allowDump);

			if (finished != null) {
				if (setSlot)
					slot.set(finished);
				return true;
			}
		}

		return false;
	}

	private static BlockEntity getShulkerBoxEntity(ItemStack shulkerBox) {
		CompoundTag cmp = ItemNBTHelper.getCompound(shulkerBox, "BlockEntityTag", false);
		if (cmp.contains("LootTable"))
			return null;

		BlockEntity te = null;
		cmp = cmp.copy();
		cmp.putString("id", "minecraft:shulker_box");
		if (shulkerBox.getItem() instanceof BlockItem) {
			Block shulkerBoxBlock = Block.byItem(shulkerBox.getItem());
			BlockState defaultState = shulkerBoxBlock.defaultBlockState();
			if (shulkerBoxBlock instanceof EntityBlock) {
				te = ((EntityBlock) shulkerBoxBlock).newBlockEntity(BlockPos.ZERO, defaultState);
				if (te != null)
					te.load(cmp);
			}
		}

		return te;
	}

	private static ItemStack tryAddToShulkerBox(Player player, ItemStack shulkerBox, ItemStack stack, Slot slot, boolean simulate, boolean useCopy, boolean allowDump) {
		if (!SimilarBlockTypeHandler.isShulkerBox(shulkerBox) || !slot.mayPickup(player))
			return null;

		BlockEntity tile = getShulkerBoxEntity(shulkerBox);

		if (tile != null) {
			LazyOptional<IItemHandler> handlerHolder = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			if (handlerHolder.isPresent()) {
				IItemHandler handler = handlerHolder.orElseGet(EmptyHandler::new);
				if (SimilarBlockTypeHandler.isShulkerBox(stack) && allowDump) {
					BlockEntity otherShulker = getShulkerBoxEntity(stack);
					if (otherShulker != null) {
						LazyOptional<IItemHandler> otherHolder = otherShulker.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
						if (otherHolder.isPresent()) {
							IItemHandler otherHandler = otherHolder.orElseGet(EmptyHandler::new);
							boolean any = false;
							for (int i = 0; i < otherHandler.getSlots(); i++) {
								ItemStack inserting = otherHandler.extractItem(i, 64, simulate);
								if (!inserting.isEmpty()) {
									ItemStack result = ItemHandlerHelper.insertItem(handler, inserting, simulate);
									if (result.isEmpty() || result.getCount() != inserting.getCount()) {
										if (simulate) {
											return shulkerBox;
										}
										any = true;
									}
								}
							}

							if (any) {
								ItemStack workStack = useCopy ? shulkerBox.copy() : shulkerBox;

								ItemNBTHelper.setCompound(workStack, "BlockEntityTag", tile.saveWithFullMetadata());
								ItemNBTHelper.setCompound(stack, "BlockEntityTag", otherShulker.saveWithFullMetadata());

								if (slot.mayPlace(workStack))
									return workStack;
							}
						}
					}
				}
				ItemStack result = ItemHandlerHelper.insertItem(handler, stack.copy(), simulate);
				boolean did = result.isEmpty() || result.getCount() != stack.getCount();

				if (did) {
					ItemStack workStack = useCopy ? shulkerBox.copy() : shulkerBox;
					if (!simulate)
						stack.setCount(result.getCount());

					ItemNBTHelper.setCompound(workStack, "BlockEntityTag", tile.saveWithFullMetadata());

					if (slot.mayPlace(workStack))
						return workStack;
				}
			}
		}

		return null;
	}

}
