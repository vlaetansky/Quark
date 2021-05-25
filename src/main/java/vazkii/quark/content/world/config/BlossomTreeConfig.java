package vazkii.quark.content.world.config;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.IConfigType;
import vazkii.quark.base.world.config.BiomeTypeConfig;
import vazkii.quark.base.world.config.DimensionConfig;

public class BlossomTreeConfig implements IConfigType {

	@Config
	public DimensionConfig dimensions = DimensionConfig.overworld(false);
	
	@Config
	public BiomeTypeConfig biomeTypes;
	
	@Config
	public int rarity;
	
	public BlossomTreeConfig(int rarity, BiomeDictionary.Type type) {
		this.rarity = rarity;
		biomeTypes = new BiomeTypeConfig(false, type);
	}
	
}
