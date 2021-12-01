package vazkii.quark.content.tools.item;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.base.item.QuarkItem;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.tools.module.AncientTomesModule;

public class AncientTomeItem extends QuarkItem {

	public AncientTomeItem(QuarkModule module) {
		super("ancient_tome", module, 
				new Item.Properties().stacksTo(1));
	}
	
	@Override
	public boolean isEnchantable(@Nonnull ItemStack stack) {
		return false;
	}
	
	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return false;
	}

	@Nonnull
	@Override
	public Rarity getRarity(ItemStack stack) {
		return EnchantedBookItem.getEnchantments(stack).isEmpty() ? super.getRarity(stack) : Rarity.UNCOMMON;
	}
	
	public static ItemStack getEnchantedItemStack(EnchantmentInstance ench) {
		ItemStack newStack = new ItemStack(AncientTomesModule.ancient_tome);
		EnchantedBookItem.addEnchantment(newStack, ench);
		return newStack;
	}
	
	@Override
	public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
		if (isEnabled() || group == CreativeModeTab.TAB_SEARCH) {
			if (group == CreativeModeTab.TAB_SEARCH || group.getEnchantmentCategories().length != 0) {
				Registry.ENCHANTMENT.forEach(ench -> {
					if ((group == CreativeModeTab.TAB_SEARCH && ench.getMaxLevel() != 1) ||
							AncientTomesModule.validEnchants.contains(ench)) {
						if ((group == CreativeModeTab.TAB_SEARCH && ench.category != null) || group.hasEnchantmentCategory(ench.category)) {
							items.add(getEnchantedItemStack(new EnchantmentInstance(ench, ench.getMaxLevel())));
						}
					}
				});
			}
		}
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		
		Enchantment ench = AncientTomesModule.getTomeEnchantment(stack);
		if(ench != null)
			tooltip.add(new TranslatableComponent("quark.misc.ancient_tome_tooltip", new TranslatableComponent(ench.getDescriptionId()), new TranslatableComponent("enchantment.level." + (ench.getMaxLevel() + 1))).withStyle(ChatFormatting.GRAY));
	}

}
