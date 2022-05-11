package vazkii.quark.content.tools.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.base.item.QuarkItem;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.experimental.module.EnchantmentsBegoneModule;
import vazkii.quark.content.tools.module.AncientTomesModule;

import javax.annotation.Nonnull;
import java.util.List;

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
	public boolean isFoil(@Nonnull ItemStack stack) {
		return true;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return false;
	}

	@Nonnull
	@Override
	public Rarity getRarity(@Nonnull ItemStack stack) {
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
				ForgeRegistries.ENCHANTMENTS.forEach(ench -> {
					if (!EnchantmentsBegoneModule.shouldBegone(ench) && ench.getMaxLevel() != 1) {
						if (!AncientTomesModule.isInitialized() || AncientTomesModule.validEnchants.contains(ench)) {
							if (group == CreativeModeTab.TAB_SEARCH || group.hasEnchantmentCategory(ench.category)) {
								items.add(getEnchantedItemStack(new EnchantmentInstance(ench, ench.getMaxLevel())));
							}
						}
					}
				});
			}
		}
	}

	public static Component getFullTooltipText(Enchantment ench) {
		return new TranslatableComponent("quark.misc.ancient_tome_tooltip", new TranslatableComponent(ench.getDescriptionId()), new TranslatableComponent("enchantment.level." + (ench.getMaxLevel() + 1))).withStyle(ChatFormatting.GRAY);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(@Nonnull ItemStack stack, Level worldIn, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);

		Enchantment ench = AncientTomesModule.getTomeEnchantment(stack);
		if(ench != null)
			tooltip.add(getFullTooltipText(ench));
	}

}
