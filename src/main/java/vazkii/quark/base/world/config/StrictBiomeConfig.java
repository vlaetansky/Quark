package vazkii.quark.base.world.config;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.world.biome.Biome;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.ConfigFlagManager;

public class StrictBiomeConfig implements IBiomeConfig {

	@Config(name = "Biomes")
	private List<String> biomeStrings;

	@Config
	private boolean isBlacklist;

	public StrictBiomeConfig(boolean isBlacklist, String... biomes) {
		this.isBlacklist = isBlacklist;

		biomeStrings = new LinkedList<>();
		biomeStrings.addAll(Arrays.asList(biomes));
	}
	
	@Override
	public boolean canSpawn(Biome b) {
		return biomeStrings.contains(b.getRegistryName().toString()) != isBlacklist;
	}

	@Override
	public void onReload(ConfigFlagManager flagManager) {
		// NO-OP
	}

}
