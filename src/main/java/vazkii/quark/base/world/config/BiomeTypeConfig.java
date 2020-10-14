package vazkii.quark.base.world.config;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.world.biome.Biome;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.ConfigFlagManager;

public class BiomeTypeConfig implements IBiomeConfig {

	@Config(name = "Biome Categories")
	@Config.Restriction({"NONE", "TAIGA", "EXTREME_HILLS", "JUNGLE", "MESA", "PLAINS", "SAVANNA", "THEEND", "BEACH", "FOREST",
			"OCEAN", "DESERT", "RIVER", "SWAMP", "MUSHROOM", "NETHER"})
	private List<String> categoryStrings;

	@Config
	private boolean isBlacklist;

	private List<Biome.Category> categories;

	public BiomeTypeConfig(boolean isBlacklist, Biome.Category... categories) {
		this.isBlacklist = isBlacklist;

		categoryStrings = new LinkedList<>();
		for (Biome.Category c : categories)
			categoryStrings.add(c.getName());
	}

	public BiomeTypeConfig(boolean isBlacklist, String... categories) {
		this.isBlacklist = isBlacklist;

		categoryStrings = new LinkedList<>();
		categoryStrings.addAll(Arrays.asList(categories));
	}
	
	@Override
	public boolean canSpawn(Biome b) {
		if (categories == null)
			updateTypes();

		Biome.Category category = b.getCategory();
		return canSpawn(category);
	}
	
	public boolean canSpawn(Biome.Category category) {
		for (Biome.Category c : categories)
			if(c == category)
				return !isBlacklist;

		return isBlacklist;
	}

	@Override
	public void onReload(ConfigFlagManager flagManager) {
		updateTypes();
	}
	
	public void updateTypes() {
		categories = new LinkedList<>();
		for (String s : categoryStrings) {
			Biome.Category category = Biome.Category.byName(s);
			
			if (category != null)
				categories.add(category);
		}
	}
}
