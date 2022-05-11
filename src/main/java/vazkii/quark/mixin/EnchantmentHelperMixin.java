package vazkii.quark.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.quark.content.experimental.module.EnchantmentsBegoneModule;
import vazkii.quark.content.tools.module.AncientTomesModule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

	@ModifyVariable(method = "getAvailableEnchantmentResults", at = @At("RETURN"))
	private static List<EnchantmentInstance> begoneEnchantments(List<EnchantmentInstance> enchantmentInstances) {
		return EnchantmentsBegoneModule.begoneEnchantmentInstances(enchantmentInstances);
	}


	@Inject(method = "getEnchantments", at = @At("HEAD"), cancellable = true)
	private static void getAncientTomeEnchantments(ItemStack stack, CallbackInfoReturnable<Map<Enchantment, Integer>> callbackInfoReturnable) {
		Enchantment enchant = AncientTomesModule.getTomeEnchantment(stack);

		if(enchant != null) {
			Map<Enchantment, Integer> map = new HashMap<>();
			map.put(enchant, 1);
			callbackInfoReturnable.setReturnValue(map);
		}
	}
}
