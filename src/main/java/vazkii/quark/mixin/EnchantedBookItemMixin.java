package vazkii.quark.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.quark.content.experimental.module.EnchantmentsBegoneModule;
import vazkii.quark.content.tools.item.PickarangItem;

@Mixin(EnchantedBookItem.class)
public class EnchantedBookItemMixin {

	@Inject(method = "fillItemCategory", at = @At("RETURN"))
	private void canApply(CreativeModeTab tab, NonNullList<ItemStack> stacks, CallbackInfo ci) {
		EnchantmentsBegoneModule.begoneItems(stacks);
	}

	private static boolean canPiercingApply(Enchantment enchantment, ItemStack stack) {
		return enchantment == Enchantments.PIERCING && stack.getItem() instanceof PickarangItem;
	}

}
