package vazkii.quark.mixin;

import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import vazkii.quark.content.experimental.module.EnchantmentsBegoneModule;

import java.util.List;

@Mixin(targets = "net.minecraft.world.entity.npc.VillagerTrades$EnchantBookForEmeralds")
public class EnchantBookForEmeraldsMixin {

	@ModifyVariable(method = "getOffer", at = @At("STORE"))
	private List<Enchantment> filterBegoneFromTrades(List<Enchantment> enchantments) {
		return EnchantmentsBegoneModule.begoneEnchantments(enchantments);
	}
}
