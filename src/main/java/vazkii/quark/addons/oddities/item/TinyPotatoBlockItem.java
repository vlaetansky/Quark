package vazkii.quark.addons.oddities.item;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.addons.oddities.block.TinyPotatoBlock;
import vazkii.quark.addons.oddities.util.TinyPotatoRenderInfo;
import vazkii.quark.api.IRuneColorProvider;
import vazkii.quark.base.handler.ContributorRewardHandler;

import javax.annotation.Nonnull;
import java.util.List;

public class TinyPotatoBlockItem extends BlockItem implements IRuneColorProvider {
	private static final int NOT_MY_NAME = 17;
	private static final List<String> TYPOS = List.of("vaskii", "vazki", "voskii", "vazkkii", "vazkki", "vazzki", "vaskki", "vozkii", "vazkil", "vaskil", "vazkill", "vaskill", "vaski");

	private static final String TICKS = "notMyNameTicks";

	public TinyPotatoBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
		return super.canEquip(stack, armorType, entity) ||
				(entity instanceof Player player && ContributorRewardHandler.getTier(player) > 0);
	}

	@Nonnull
	@Override
	public String getDescriptionId(@Nonnull ItemStack stack) {
		if (TinyPotatoBlock.isAngry(stack))
			return super.getDescriptionId(stack) + ".angry";
		return super.getDescriptionId(stack);
	}

	@Override
	public void inventoryTick(@Nonnull ItemStack stack, @Nonnull Level world, @Nonnull Entity holder, int itemSlot, boolean isSelected) {
		if (!world.isClientSide && holder instanceof Player player && holder.tickCount % 30 == 0 && TYPOS.contains(ChatFormatting.stripFormatting(stack.getDisplayName().getString()))) {
			int ticks = ItemNBTHelper.getInt(stack, TICKS, 0);
			if (ticks < NOT_MY_NAME) {
				player.sendMessage(new TranslatableComponent("quark.misc.you_came_to_the_wrong_neighborhood." + ticks).withStyle(ChatFormatting.RED), Util.NIL_UUID);
				ItemNBTHelper.setInt(stack, TICKS, ticks + 1);
			}
		}
	}

	@Override
	public boolean isFoil(@Nonnull ItemStack stack) {
		if (stack.hasCustomHoverName() && TinyPotatoRenderInfo.fromComponent(stack.getHoverName()).enchanted())
			return true;
		return super.isFoil(stack);
	}

	@Override
	public int getRuneColor(ItemStack stack) {
		if (stack.hasCustomHoverName())
			return TinyPotatoRenderInfo.fromComponent(stack.getHoverName()).runeColor();
		return -1;
	}
}
