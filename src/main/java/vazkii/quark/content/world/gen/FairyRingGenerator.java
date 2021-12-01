package vazkii.quark.content.world.gen;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.BiomeCategory;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.Tags;
import vazkii.quark.base.module.config.type.DimensionConfig;
import vazkii.quark.base.world.generator.Generator;
import vazkii.quark.content.world.module.FairyRingsModule;

public class FairyRingGenerator extends Generator {

	public FairyRingGenerator(DimensionConfig dimConfig) {
		super(dimConfig);
	}
	
	@Override
	public void generateChunk(WorldGenRegion worldIn, ChunkGenerator generator, Random rand, BlockPos corner) {
		int x = corner.getX() + rand.nextInt(16);
		int z = corner.getZ() + rand.nextInt(16);
		BlockPos center = new BlockPos(x, 128, z);
		
		Biome biome = getBiome(worldIn, center, false);
		
		Biome.BiomeCategory category = biome.getBiomeCategory();
		double chance = 0;
		if(category == BiomeCategory.FOREST)
			chance = FairyRingsModule.forestChance;
		else if(category == BiomeCategory.PLAINS)
			chance = FairyRingsModule.plainsChance;
		
		if(rand.nextDouble() < chance) {
			BlockPos pos = center;
			BlockState state = worldIn.getBlockState(pos);
			
			while(state.getMaterial() != Material.GRASS && pos.getY() > 30) {
				pos = pos.below();
				state = worldIn.getBlockState(pos);
			}
			
			if(state.getMaterial() == Material.GRASS)
				spawnFairyRing(worldIn, pos.below(), rand);
		}		
	}
	
	public static void spawnFairyRing(LevelAccessor world, BlockPos pos, Random rand) {
		BlockState flower = Blocks.OXEYE_DAISY.defaultBlockState();
		
		for(int i = -3; i <= 3; i++)
			for(int j = -3; j <= 3; j++) {
				float dist = (i * i) + (j * j);
				if(dist < 7 || dist > 10)
					continue;
				
				for(int k = 5; k > -4; k--) {
					BlockPos fpos = pos.offset(i, k, j);
					BlockPos fposUp = fpos.above();
					BlockState state = world.getBlockState(fpos);	
					if(state.getMaterial() == Material.GRASS && world.isEmptyBlock(fposUp)) {
						world.setBlock(fpos.above(), flower, 2);
						break;
					}
				}
			}
		
		BlockPos orePos = pos.below(rand.nextInt(10) + 25);
		BlockState stoneState = world.getBlockState(orePos);
		int down = 0;
		while(!stoneState.is(Tags.Blocks.STONE) && down < 10) {
			orePos = orePos.below();	
			stoneState = world.getBlockState(orePos);	
			down++;
		}
		
		if(stoneState.is(Tags.Blocks.STONE)) {
			BlockState ore = FairyRingsModule.ores.get(rand.nextInt(FairyRingsModule.ores.size()));
			world.setBlock(orePos, ore, 2);
			for(Direction face : Direction.values())
				if(rand.nextBoolean())
					world.setBlock(orePos.relative(face), ore, 2);
		}
	}
	
}
