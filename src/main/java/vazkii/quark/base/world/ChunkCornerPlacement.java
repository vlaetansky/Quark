package vazkii.quark.base.world;

import java.util.Random;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class ChunkCornerPlacement extends PlacementModifier {

	private static final ChunkCornerPlacement INSTANCE = new ChunkCornerPlacement();
	public static final Codec<ChunkCornerPlacement> CODEC = Codec.unit(() -> {
		return INSTANCE;
	});

	@Override // getPositions
	public Stream<BlockPos> getPositions(PlacementContext wdc, Random random, BlockPos pos) {
		return ImmutableSet.of(pos).stream();
	}

	@Override
	public PlacementModifierType<?> type() {
		return WorldGenHandler.CHUNK_CORNER_PLACEMENT_TYPE;
	}

}
