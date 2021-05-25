package vazkii.quark.base.world.config;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.ConfigFlagManager;

public class BiomeConfig implements IBiomeConfig {

	@Config(description = "Types of biomes this should spawn in. Must match both this and 'biomes' to spawn.")
	BiomeTypeConfig types;
	
	@Config(description = "Biome names this should spawn in. Must match both this and 'types' to spawn.")
	StrictBiomeConfig biomes;
	
	private BiomeConfig(BiomeTypeConfig types, StrictBiomeConfig biomes) {
		this.types = types;
		this.biomes = biomes;
	}
	
	public static BiomeConfig fromBiomeTypes(boolean isBlacklist, BiomeDictionary.Type... typesIn) {
		return new BiomeConfig(new BiomeTypeConfig(isBlacklist, typesIn), noSBC());
	}
	
	public static BiomeConfig fromBiomeTypeStrings(boolean isBlacklist, String... typesIn) {
		return new BiomeConfig(new BiomeTypeConfig(isBlacklist, typesIn), noSBC());
	}
	
	public static BiomeConfig fromBiomeReslocs(boolean isBlacklist, String... typesIn) {
		return new BiomeConfig(noBTC(), new StrictBiomeConfig(isBlacklist, typesIn));
	}
	
	public static BiomeConfig all() {
		return new BiomeConfig(noBTC(), noSBC());
	}
	
	private static BiomeTypeConfig noBTC() {
		return new BiomeTypeConfig(true, new BiomeDictionary.Type[0]);
	}
	
	
	private static StrictBiomeConfig noSBC() {
		return new StrictBiomeConfig(true, new String[0]);
	}
	
	@Override
	public void onReload(ConfigFlagManager flagManager) {
		types.onReload(flagManager);
		biomes.onReload(flagManager);
	}
	
	@Override
	public boolean canSpawn(ResourceLocation b) {
		return b != null && types.canSpawn(b) && biomes.canSpawn(b);
	}

}
