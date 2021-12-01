package vazkii.quark.base.world;

import java.util.Random;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.placement.DecorationContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

public class ChunkCornerPlacement extends FeatureDecorator<NoneDecoratorConfiguration> {

	public ChunkCornerPlacement() {
		super(NoneDecoratorConfiguration.CODEC);
	}

	@Override // getPositions
	public Stream<BlockPos> getPositions(DecorationContext wdc, Random random, NoneDecoratorConfiguration config, BlockPos pos) {
		return ImmutableSet.of(pos).stream();
	}

}
