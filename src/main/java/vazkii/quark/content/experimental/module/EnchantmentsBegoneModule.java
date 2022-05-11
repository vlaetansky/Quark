package vazkii.quark.content.experimental.module;

import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

import java.util.List;

@LoadModule(category = ModuleCategory.EXPERIMENTAL, enabledByDefault = false)
public class EnchantmentsBegoneModule extends QuarkModule {

	@Config
	public static List<String> enchantmentsToBegone = Lists.newArrayList();

	private static boolean staticEnabled;

	private static final List<Enchantment> enchantments = Lists.newArrayList();

	@Override
	public void configChanged() {
		staticEnabled = enabled;

		enchantments.clear();

		for (String enchantKey : enchantmentsToBegone) {
			Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchantKey));
			if (enchantment != null)
				enchantments.add(enchantment);
		}
	}

	public static List<Enchantment> begoneEnchantments(List<Enchantment> list) {
		if (!staticEnabled)
			return list;

		return list.stream().filter(enchantments::contains).toList();
	}

	public static List<EnchantmentInstance> begoneEnchantmentInstances(List<EnchantmentInstance> list) {
		if (!staticEnabled)
			return list;

		return list.stream().filter(it -> enchantments.contains(it.enchantment)).toList();
	}
}
