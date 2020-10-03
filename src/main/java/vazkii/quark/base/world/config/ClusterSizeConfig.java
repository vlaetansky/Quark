package vazkii.quark.base.world.config;

import net.minecraftforge.common.BiomeDictionary;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.IConfigType;

public class ClusterSizeConfig implements IConfigType {

	@Config
	public DimensionConfig dimensions = DimensionConfig.overworld(false);

	@Config
	public BiomeTypeConfig biomes;

	@Config
	@Config.Min(0)
	public int rarity;

	@Config
	@Config.Min(0)
	@Config.Max(255)
	public int minYLevel = 0;

	@Config
	@Config.Min(0)
	@Config.Max(255)
	public int maxYLevel = 64;

	@Config
	@Config.Min(0)
	public int horizontalSize;

	@Config
	@Config.Min(0)
	public int verticalSize;

	@Config
	@Config.Min(0)
	public int horizontalVariation;

	@Config
	@Config.Min(0)
	public int verticalVariation;
	
	public ClusterSizeConfig(int rarity, int horizontal, int vertical, int horizontalVariation, int verticalVariation, boolean isBlacklist, BiomeDictionary.Type... types) {
		this.rarity = rarity;
		this.horizontalSize = horizontal;
		this.verticalSize = vertical;
		this.horizontalVariation = horizontalVariation;
		this.verticalVariation = verticalVariation;
		biomes = new BiomeTypeConfig(isBlacklist, types);
	}
	
	public ClusterSizeConfig setYLevels(int min, int max) {
		this.minYLevel = min;
		this.maxYLevel = max;
		return this;
	}

	
}
