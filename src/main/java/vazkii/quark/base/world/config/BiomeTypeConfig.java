package vazkii.quark.base.world.config;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.ConfigFlagManager;
import vazkii.quark.base.module.config.IConfigType;

public class BiomeTypeConfig implements IBiomeConfig {

	@Config(name = "Biome Types")
	@Config.Restriction({"HOT", "COLD", "SPARSE", "DENSE", "WET", "SAVANNA", "CONIFEROUS", "JUNGLE", "SPOOKY", "DEAD",
			"LUSH", "NETHER", "END", "MUSHROOM", "MAGICAL", "RARE", "OCEAN", "RIVER", "WATER", "MESA", "FOREST",
			"PLAINS", "MOUNTAIN", "HILLS", "SWAMP", "SANDY", "SNOWY", "WASTELAND", "BEACH", "VOID"})
	private List<String> typeStrings;

	@Config
	private boolean isBlacklist;

	private List<BiomeDictionary.Type> types;

	public BiomeTypeConfig(boolean isBlacklist, BiomeDictionary.Type... types) {
		this.isBlacklist = isBlacklist;

		typeStrings = new LinkedList<>();
		for (BiomeDictionary.Type s : types)
			typeStrings.add(s.getName());
	}

	public BiomeTypeConfig(boolean isBlacklist, String... types) {
		this.isBlacklist = isBlacklist;

		typeStrings = new LinkedList<>();
		typeStrings.addAll(Arrays.asList(types));
	}
	
	@Override
	public boolean canSpawn(Biome b) {
		if (types == null)
			updateTypes();

		Set<BiomeDictionary.Type> currentTypes = BiomeDictionary.getTypes(b);

		for (BiomeDictionary.Type type : types)
			if (currentTypes.contains(type))
				return !isBlacklist;

		return isBlacklist;
	}

	@Override
	public void onReload(ConfigFlagManager flagManager) {
		updateTypes();
	}
	
	public void updateTypes() {
		types = new LinkedList<>();
		for (String s : typeStrings) {
			BiomeDictionary.Type type = BiomeDictionary.Type.getType(s);
			if (type != null)
				types.add(type);
		}
	}
}
