package vazkii.quark.addons.oddysey.feature;

import java.util.Random;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import vazkii.quark.content.world.module.underground.CaveCrystalUndergroundBiomeModule;

public class SpiralPillarFeature extends Feature<NoFeatureConfig> {

	public SpiralPillarFeature(Codec<NoFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean func_241855_a(ISeedReader world, ChunkGenerator chunk, Random rand, BlockPos pos, NoFeatureConfig config) {
		int base = 3 + rand.nextInt(2);
		int part2 = base + 8 + rand.nextInt(5);
		int part3 = part2 + 10 + rand.nextInt(5);
		int part4 = part3 + 12 + rand.nextInt(8);
		int y = -4;
		
		Supplier<BlockState> prov = () -> (rand.nextFloat() < 0.05 ? Blocks.GILDED_BLACKSTONE :Blocks.BLACKSTONE).getDefaultState();
		pos = pos.up();
		
		for(; y < base; y++) {
			for(int i = -2; i <= 2; i++)
				for(int j = -2; j <= 2; j++)
				world.setBlockState(pos.add(i, y, j), prov.get(), 2);
		}
		
		for(; y < part2; y++) {
			for(int i = -1; i <= 1; i++)
				for(int j = -1; j <= 1; j++)
					world.setBlockState(pos.add(i, y, j), prov.get(), 2);
		}
		
		for(; y < part3; y++) {
			for(int i = -1; i <= 1; i++)
				for(int j = -1; j <= 1; j++)
					if(i == 0 | j == 0)
						world.setBlockState(pos.add(i, y, j), prov.get(), 2);
		}
		
		for(; y < part4; y++)
			world.setBlockState(pos.add(0, y, 0), prov.get(), 2);
		
		int steps = 80 + rand.nextInt(30);
		int substeps = 10;
		
		int fullSteps = steps * substeps;
		int deteroirate = (int) ((0.5 + rand.nextDouble() * 0.3) * fullSteps);
		
		double spin = 0.12 + rand.nextDouble() * 0.16;
		double spread = 0.12 + rand.nextDouble() * 0.04;
		double upwardMotion = rand.nextDouble() * 0.2;
		
		if(rand.nextBoolean())
			spin *= -1;
		
		ImmutableSet<Block> blocks = ImmutableSet.of(Blocks.BLUE_STAINED_GLASS, Blocks.RED_STAINED_GLASS, Blocks.PURPLE_STAINED_GLASS, Blocks.PINK_STAINED_GLASS);
		BlockState state = blocks.asList().get(rand.nextInt(blocks.size())).getDefaultState();
		
		for(int i = 0; i < fullSteps; i++) {
			double t = (double) i * spin;
			int x = (int) (Math.sin(t / substeps) * i * spread / substeps);
			int z = (int) (Math.cos(t / substeps) * i * spread / substeps);
			int yp = y + (int) Math.round(((double) i / substeps) * upwardMotion);
			
			BlockPos next = pos.add(x, yp, z);
			
			float chance = 1F;
			if(i > deteroirate) {
				int deterStep = i - deteroirate;
				int maxSteps = (fullSteps - deteroirate);
				chance -= (float) deterStep / (float) maxSteps;
			}
			
			if(rand.nextFloat() < chance)
				world.setBlockState(next, state, 2);
		}
		
		world.setBlockState(pos.add(0, y, 0), Blocks.GLOWSTONE.getDefaultState(), 2);
		
		return false;
	}
	
}
