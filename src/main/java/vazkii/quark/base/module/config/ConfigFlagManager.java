package vazkii.quark.base.module.config;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.crafting.CraftingHelper;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.recipe.ingredient.FlagIngredient;
import vazkii.quark.base.recipe.ingredient.PotionIngredient;

public final class ConfigFlagManager {

	public static LootItemConditionType flagConditionType;

	private final Map<String, Boolean> flags = new HashMap<>();
	
	public ConfigFlagManager() {
		CraftingHelper.register(new FlagRecipeCondition.Serializer(this, new ResourceLocation(Quark.MOD_ID, "flag")));
		flagConditionType = new LootItemConditionType(new FlagLootCondition.FlagSerializer(this));
		Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(Quark.MOD_ID, "flag"), flagConditionType);

		CraftingHelper.register(new ResourceLocation(Quark.MOD_ID, "potion"), PotionIngredient.Serializer.INSTANCE);
		CraftingHelper.register(new ResourceLocation(Quark.MOD_ID, "flag"),  new FlagIngredient.Serializer(this));
	}
	
	public void clear() {
		flags.clear();
	}
	
	public void putFlag(QuarkModule module, String flag, boolean value) {
		flags.put(flag, value && module.enabled);
	}
	
	public void putEnabledFlag(QuarkModule module) {
		flags.put(module.lowercaseName, module.enabled);
	}
	
	public boolean isValidFlag(String flag) {
		return flags.containsKey(flag);
	}
	
	public boolean getFlag(String flag) {
		Boolean obj = flags.get(flag);
		return obj != null && obj;
	}
	
}
