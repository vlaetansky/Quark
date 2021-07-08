package vazkii.quark.content.world.config;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.type.BiomeConfig;
import vazkii.quark.base.module.config.type.BiomeTypeConfig;
import vazkii.quark.base.module.config.type.DimensionConfig;
import vazkii.quark.base.module.config.type.IConfigType;

public class BlossomTreeConfig implements IConfigType {

	@Config
	public DimensionConfig dimensions = DimensionConfig.overworld(false);
	
	@Config
	public BiomeConfig biomeConfig;
	
	@Config
	public int rarity;
	
	public BlossomTreeConfig(int rarity, BiomeDictionary.Type type) {
		this.rarity = rarity;
		biomeConfig = BiomeConfig.fromBiomeTypes(false, type);
	}
	
}
