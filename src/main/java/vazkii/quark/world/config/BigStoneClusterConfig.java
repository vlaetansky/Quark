package vazkii.quark.world.config;

import net.minecraftforge.common.BiomeDictionary;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.world.config.BiomeTypeConfig;
import vazkii.quark.base.world.config.ClusterSizeConfig;
import vazkii.quark.base.world.config.DimensionConfig;

public class BigStoneClusterConfig extends ClusterSizeConfig {

	@Config
	public boolean enabled = true;

	public BigStoneClusterConfig(BiomeDictionary.Type... types) {
		this(DimensionConfig.overworld(false), 14, 9, 4, 20, 80, types);
	}

	public BigStoneClusterConfig(DimensionConfig dimensions, int clusterSize, int sizeVariation, int rarity, int minYLevel, int maxYLevel, BiomeDictionary.Type... types) {
		super(rarity, clusterSize, clusterSize, sizeVariation, sizeVariation, false, types);
		this.dimensions = dimensions;
		biomes = new BiomeTypeConfig(false, types);
		
		this.minYLevel = minYLevel;
		this.maxYLevel = maxYLevel;
	}

}
