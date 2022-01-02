package vazkii.quark.content.world.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.EnvironmentScanPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RandomOffsetPlacement;

public class TestFeature extends Feature<NoneFeatureConfiguration> {

	public TestFeature() {
		super(NoneFeatureConfiguration.CODEC);
	}
	
	public static PlacedFeature placed(ConfiguredFeature<NoneFeatureConfiguration, ?> f) {
		return f.placed(CountPlacement.of(25), 
				InSquarePlacement.spread(), 
				PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, 
				EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12), 
				RandomOffsetPlacement.vertical(ConstantInt.of(-1)), 
				BiomeFilter.biome());
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> config) {
		WorldGenLevel worldgenlevel = config.level();
		BlockPos blockpos = config.origin();

		worldgenlevel.setBlock(blockpos, Blocks.GLOWSTONE.defaultBlockState(), 0);
		
		return true;
	}

}
