package vazkii.quark.mixin;

import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.potion.PotionBrewing;
import net.minecraft.potion.Potion;
import net.minecraft.item.crafting.Ingredient;

import java.util.List;

@Mixin(PotionBrewing.class)
public interface PotionBrewingAccessor {
	@Accessor("POTION_TYPE_CONVERSIONS")
	public static List<PotionBrewing.MixPredicate<Potion>> getPotionTypeConversions() {
		throw new AssertionError();
	}
}
