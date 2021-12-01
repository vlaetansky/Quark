package vazkii.quark.content.automation.block;

import java.util.Random;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.automation.entity.GravisandEntity;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class GravisandBlock extends QuarkBlock {

	public GravisandBlock(String regname, QuarkModule module, CreativeModeTab creativeTab, Properties properties) {
		super(regname, module, creativeTab, properties);
	}
	
	@Override
	public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
		checkRedstone(world, pos);
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		checkRedstone(worldIn, pos);
	}

	private void checkRedstone(Level worldIn, BlockPos pos) {
        boolean powered = worldIn.hasNeighborSignal(pos);

        if(powered)
        	worldIn.getBlockTicks().scheduleTick(pos, this, 2);
	}
	
	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}
	
	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
		return 15;
	}

	@Override
	public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand) {
		if(!worldIn.isClientSide) {
			if(checkFallable(worldIn, pos))
				for(Direction face : Direction.values()) {
					BlockPos offPos = pos.relative(face);
					BlockState offState = worldIn.getBlockState(offPos);
					
					if(offState.getBlock() == this)
			        	worldIn.getBlockTicks().scheduleTick(offPos, this, 2);
				}
		}
	}

	private boolean checkFallable(Level worldIn, BlockPos pos) {
		if(!worldIn.isClientSide) {
			if(tryFall(worldIn, pos, Direction.DOWN))
				return true;
			else return tryFall(worldIn, pos, Direction.UP);
		}
		
		return false;
	}
	
	private boolean tryFall(Level worldIn, BlockPos pos, Direction facing) {
		BlockPos target = pos.relative(facing);
		if((worldIn.isEmptyBlock(target) || canFallThrough(worldIn, pos, worldIn.getBlockState(target))) && pos.getY() >= 0) {
			GravisandEntity entity = new GravisandEntity(worldIn, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, facing.getStepY());
			worldIn.addFreshEntity(entity);
			return true;
		}
		
		return false;
	}
	
    public static boolean canFallThrough(LevelReader world, BlockPos pos, BlockState state) {
		Block block = state.getBlock();
		Material material = state.getMaterial();
		return state.isAir(world, pos) || block == Blocks.FIRE || material.isLiquid() || material.isReplaceable();
    }

}
