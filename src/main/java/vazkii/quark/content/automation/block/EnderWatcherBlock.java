package vazkii.quark.content.automation.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.automation.tile.EnderWatcherTileEntity;

public class EnderWatcherBlock extends QuarkBlock {
	
	public static final BooleanProperty WATCHED = BooleanProperty.create("watched");
	public static final IntegerProperty POWER = BlockStateProperties.POWER;

	public EnderWatcherBlock(QuarkModule module) {
		super("ender_watcher", module, CreativeModeTab.TAB_REDSTONE, 
				Block.Properties.of(Material.METAL, MaterialColor.COLOR_GREEN)
				.strength(3F, 10F)
				.sound(SoundType.METAL));
		
		registerDefaultState(defaultBlockState().setValue(WATCHED, false).setValue(POWER, 0));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(WATCHED, POWER);
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}
	
	@Override
	public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
		return blockState.getValue(POWER);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
		return new EnderWatcherTileEntity();
	}

}
