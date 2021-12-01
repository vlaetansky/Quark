package vazkii.quark.content.world.gen.underground;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import vazkii.quark.content.world.gen.UndergroundBiomeGenerator.Context;
import vazkii.quark.content.world.module.underground.PermafrostUndergroundBiomeModule;

public class PermafrostUndergroundBiome extends BasicUndergroundBiome {
	
	public PermafrostUndergroundBiome() {
		super(Blocks.PACKED_ICE.defaultBlockState(), PermafrostUndergroundBiomeModule.permafrost.defaultBlockState(), PermafrostUndergroundBiomeModule.permafrost.defaultBlockState(), true);
	}
	
	@Override
	public void fillFloor(Context context, BlockPos pos, BlockState state) {
		super.fillFloor(context, pos, state);

		LevelAccessor world = context.world;
		if(context.random.nextDouble() < 0.015) {
			int height = 3 + context.random.nextInt(3);
			for(int i = 0; i < height; i++) {
				pos = pos.above();
				BlockState stateAt = world.getBlockState(pos);
				
				if(world.getBlockState(pos).getBlock().isAir(stateAt, world, pos))
					world.setBlock(pos, floorState, 2);
				else break;
			}
		}
	}

}
