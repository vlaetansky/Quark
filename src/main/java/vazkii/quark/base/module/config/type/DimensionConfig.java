package vazkii.quark.base.module.config.type;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import vazkii.quark.base.module.config.Config;

public class DimensionConfig extends AbstractConfigType {

	@Config
	private boolean isBlacklist;
	@Config
	private List<String> dimensions;

	public DimensionConfig(boolean blacklist, String... dims) {
		isBlacklist = blacklist;

		dimensions = new LinkedList<>();
		Collections.addAll(dimensions, dims);
	}

	public static DimensionConfig overworld(boolean blacklist) {
		return new DimensionConfig(blacklist, "minecraft:overworld");
	}

	public static DimensionConfig nether(boolean blacklist) {
		return new DimensionConfig(blacklist, "minecraft:the_nether");
	}

	public static DimensionConfig end(boolean blacklist) {
		return new DimensionConfig(blacklist, "minecraft:the_end");
	}

	public static DimensionConfig all() {
		return new DimensionConfig(true);
	}
	
	@SuppressWarnings("unchecked")
	public boolean canSpawnHere(ChunkGenerator chunkGen) {
		Registry<LevelStem> levelStems = (Registry<LevelStem>) Registry.REGISTRY.get(Registry.LEVEL_STEM_REGISTRY);
		
		for(LevelStem stem : levelStems) {
			ChunkGenerator stemGen = stem.generator();
			if(stemGen == chunkGen)
				return canSpawnHere(stem.typeHolder().value().effectsLocation());
		}
		
		return false;
	}

	public boolean canSpawnHere(LevelAccessor world) {
		if (world == null || !(world instanceof Level))
			return false;

		return canSpawnHere(((Level) world).dimension().location());
	}
	
	public boolean canSpawnHere(ResourceLocation resloc) {
		 return dimensions.contains(resloc.toString()) != isBlacklist;
	}

}
