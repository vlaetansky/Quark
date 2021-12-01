package vazkii.quark.base.world;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class DeferedFeature extends Feature<NoneFeatureConfiguration> {

	private final GenerationStep.Decoration stage;

	public DeferedFeature(GenerationStep.Decoration stage) {
		super(NoneFeatureConfiguration.CODEC);
		this.stage = stage;
	}

	@Override
	public boolean place(WorldGenLevel seedReader, ChunkGenerator generator, Random rand, BlockPos pos, NoneFeatureConfiguration config) {
		WorldGenHandler.generateChunk(seedReader, generator, pos, stage);
		return true;
	}

}
