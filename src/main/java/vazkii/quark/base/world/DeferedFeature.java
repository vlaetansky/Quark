package vazkii.quark.base.world;

import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class DeferedFeature extends Feature<NoneFeatureConfiguration> {

	private final GenerationStep.Decoration stage;

	public DeferedFeature(GenerationStep.Decoration stage) {
		super(NoneFeatureConfiguration.CODEC);
		this.stage = stage;
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		WorldGenHandler.generateChunk(context, stage);
		return false;
	}

}
