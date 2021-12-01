package vazkii.quark.base.world;

import java.util.Random;
import java.util.function.BooleanSupplier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class ConditionalConfiguredFeature<FC extends FeatureConfiguration, F extends Feature<FC>> extends ConfiguredFeature<FC, F> {

	public final ConfiguredFeature<FC, F> parent;
	public final BooleanSupplier condition;

	public ConditionalConfiguredFeature(ConfiguredFeature<FC, F> parent, BooleanSupplier condition) {
		super(parent.feature, parent.config);
		this.parent = parent;
		this.condition = condition;
	}

	@Override // place
	public boolean place(WorldGenLevel p_242765_1_, ChunkGenerator p_242765_2_, Random p_242765_3_, BlockPos p_242765_4_) {
		return condition.getAsBoolean() && super.place(p_242765_1_, p_242765_2_, p_242765_3_, p_242765_4_);
	}

}
