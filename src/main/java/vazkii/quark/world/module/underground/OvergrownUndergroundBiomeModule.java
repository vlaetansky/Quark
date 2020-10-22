package vazkii.quark.world.module.underground;

import net.minecraft.world.biome.Biome;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.world.config.UndergroundBiomeConfig;
import vazkii.quark.world.gen.underground.OvergrownUndergroundBiome;

@LoadModule(category = ModuleCategory.WORLD)
public class OvergrownUndergroundBiomeModule extends UndergroundBiomeModule {

	@Override
	protected UndergroundBiomeConfig getBiomeConfig() {
		return new UndergroundBiomeConfig(new OvergrownUndergroundBiome(), 80, Biome.Category.FOREST);
	}
	
	@Override
	protected String getBiomeName() {
		return "overgrown";
	}

}
