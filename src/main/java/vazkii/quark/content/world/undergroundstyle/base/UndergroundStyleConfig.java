package vazkii.quark.content.world.undergroundstyle.base;

import net.minecraftforge.common.BiomeDictionary;
import vazkii.quark.base.module.config.type.ClusterSizeConfig;
import vazkii.quark.base.module.config.type.IBiomeConfig;

public class UndergroundStyleConfig<T extends UndergroundStyle> extends ClusterSizeConfig {

	public final T biomeObj;

	public UndergroundStyleConfig(T biomeObj, int rarity, boolean isBlacklist, BiomeDictionary.Type... categories) {
		super(rarity, 26, 14, 14, 6, isBlacklist, categories);
		this.biomeObj = biomeObj;
	}

	public UndergroundStyleConfig(T biomeObj, int rarity, BiomeDictionary.Type... categories) {
		this(biomeObj, rarity, false, categories);
	}
	
	public UndergroundStyleConfig(T biomeObj, int rarity, int horizontal, int vertical, int horizontalVariation, int verticalVariation, IBiomeConfig config) {
		super(rarity, horizontal, vertical, horizontalVariation, verticalVariation, config);
		this.biomeObj = biomeObj;
	}
	
	public UndergroundStyleConfig<T> setDefaultSize(int horizontal, int vertical, int horizontalVariation, int verticalVariation) {
		this.horizontalSize = horizontal;
		this.verticalSize = vertical;
		this.horizontalVariation = horizontalVariation;
		this.verticalVariation = verticalVariation;
		return this;
	}

}
