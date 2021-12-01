package vazkii.quark.content.world.block;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LayerLightEngine;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.ToolType;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.QuarkModule;

public class GlowceliumBlock extends QuarkBlock {

	public GlowceliumBlock(QuarkModule module) {
		super("glowcelium", module, CreativeModeTab.TAB_BUILDING_BLOCKS,
				Block.Properties.of(Material.GRASS, MaterialColor.COLOR_LIGHT_BLUE)
						.randomTicks()
						.strength(0.5F)
						.lightLevel(b -> 7)
						.harvestTool(ToolType.SHOVEL)
						.sound(SoundType.GRASS));
	}

	@Override
	public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random random) {
		if(!worldIn.isClientSide) {
			if(!canExist(state, worldIn, pos))
				worldIn.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
			else for(int i = 0; i < 4; ++i) {
				BlockPos blockpos = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                if(worldIn.getBlockState(blockpos).getBlock() == Blocks.DIRT && canGrowTo(state, worldIn, blockpos)) 
					worldIn.setBlockAndUpdate(blockpos, defaultBlockState());
			}
		}
	}

	// Some vanilla copypasta from SpreadableSnowyDirtBlock
	
	private static boolean canExist(BlockState state, LevelReader world, BlockPos pos) {
		BlockPos blockpos = pos.above();
		BlockState blockstate = world.getBlockState(blockpos);
		int i = LayerLightEngine.getLightBlockInto(world, state, pos, blockstate, blockpos, Direction.UP, blockstate.getLightBlock(world, blockpos));
		return i < world.getMaxLightLevel();
	}

	private static boolean canGrowTo(BlockState state, LevelReader world, BlockPos pos) {
		BlockPos blockpos = pos.above();
		return canExist(state, world, pos) && !world.getFluidState(blockpos).is(FluidTags.WATER);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	   public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {
		super.animateTick(stateIn, worldIn, pos, rand);

		if(rand.nextInt(40) == 0)
			worldIn.addParticle(ParticleTypes.END_ROD, pos.getX() + rand.nextDouble(), pos.getY() + 1.15, pos.getZ() + rand.nextDouble(), 0, 0, 0);
	}
	
	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
		return Blocks.MYCELIUM.canSustainPlant(state, world, pos, facing, plantable);
	}

}
