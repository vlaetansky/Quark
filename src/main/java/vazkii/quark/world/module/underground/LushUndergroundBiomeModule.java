package vazkii.quark.world.module.underground;

import net.minecraft.world.biome.Biome;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.world.config.UndergroundBiomeConfig;
import vazkii.quark.world.gen.underground.LushUndergroundBiome;

@LoadModule(category = ModuleCategory.WORLD)
public class LushUndergroundBiomeModule extends UndergroundBiomeModule {

	@Override
	protected UndergroundBiomeConfig getBiomeConfig() {
		return new UndergroundBiomeConfig(new LushUndergroundBiome(), 80, Biome.Category.JUNGLE);
	}

	@Override
	protected String getBiomeName() {
		return "lush";
	}
	
}
