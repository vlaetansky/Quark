package vazkii.quark.addons.oddysey.module;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.addons.oddysey.biome.BlackDesertBiome;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;

@LoadModule(category = ModuleCategory.ODYSSEY, requiredMod = Quark.ODYSSEY_ID, hasSubscriptions = true)
public class BlackDesertModule extends QuarkModule {

	@Override
	public void construct() {
		RegistryHelper.register(BlackDesertBiome.biome(), BlackDesertBiome.ID);
		BiomeManager.addBiome(BiomeType.DESERT, new BiomeEntry(RegistryKey.getOrCreateKey(Registry.BIOME_KEY, new ResourceLocation(BlackDesertBiome.ID)), 20));
	}
	
}
