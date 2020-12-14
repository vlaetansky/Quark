package vazkii.quark.addons.oddysey.feature;

import java.util.Random;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class BlackstoneSpikeFeature extends Feature<NoFeatureConfig> {

	public BlackstoneSpikeFeature(Codec<NoFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean func_241855_a(ISeedReader world, ChunkGenerator chunk, Random rand, BlockPos pos, NoFeatureConfig config) {
		int height = rand.nextInt(3) + 1;
		int y = 0;
		
		Supplier<BlockState> prov = () -> (rand.nextFloat() < 0.3 ? Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS : Blocks.POLISHED_BLACKSTONE_BRICKS).getDefaultState();
		for(; y < height; y++)
			world.setBlockState(pos.up(y), prov.get(), 2);
		
		world.setBlockState(pos.up(y), Blocks.CHISELED_POLISHED_BLACKSTONE.getDefaultState(), 2);
		
		return false;
	}
	

}
