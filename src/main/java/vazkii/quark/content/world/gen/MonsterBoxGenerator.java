package vazkii.quark.content.world.gen;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.server.level.WorldGenRegion;
import vazkii.quark.base.module.config.type.DimensionConfig;
import vazkii.quark.base.world.generator.Generator;
import vazkii.quark.content.world.module.MonsterBoxModule;

public class MonsterBoxGenerator extends Generator {

	public MonsterBoxGenerator(DimensionConfig dimConfig) {
		super(dimConfig);
	}

	@Override
	public void generateChunk(WorldGenRegion world, ChunkGenerator generator, Random rand, BlockPos chunkCorner) {
		if(generator instanceof FlatLevelSource)
			return;
		
		double chance =  MonsterBoxModule.chancePerChunk;
		
		while(rand.nextDouble() <= chance) {
				BlockPos pos = chunkCorner.offset(rand.nextInt(16), MonsterBoxModule.minY + rand.nextInt(MonsterBoxModule.maxY - MonsterBoxModule.minY + 1), rand.nextInt(16));
			if(world.isEmptyBlock(pos)) {
				BlockPos testPos = pos;
				BlockState testState;
				int moves = 0;
				
				do {
					testPos = testPos.below();
					testState = world.getBlockState(testPos);
					moves++;
				} while(moves < MonsterBoxModule.searchRange && testState.getMaterial() != Material.STONE && testPos.getY() >= MonsterBoxModule.minY);
				
				BlockPos placePos = testPos.above();
				if(testPos.getY() >= MonsterBoxModule.minY && world.isEmptyBlock(placePos) && world.getBlockState(placePos.below()).isFaceSturdy(world, placePos.below(), Direction.UP))
					world.setBlock(placePos, MonsterBoxModule.monster_box.defaultBlockState(), 0);
			}
			
			chance -=MonsterBoxModule.chancePerChunk;
		}
	}

}
