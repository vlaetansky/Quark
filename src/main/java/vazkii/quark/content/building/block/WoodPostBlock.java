package vazkii.quark.content.building.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.Lantern;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolType;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.handler.RenderLayerHandler.RenderTypeSkeleton;
import vazkii.quark.base.module.QuarkModule;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class WoodPostBlock extends QuarkBlock implements SimpleWaterloggedBlock {

	private static final VoxelShape SHAPE_X = Block.box(0F, 6F, 6F, 16F, 10F, 10F);
	private static final VoxelShape SHAPE_Y = Block.box(6F, 0F, 6F, 10F, 16F, 10F);
	private static final VoxelShape SHAPE_Z = Block.box(6F, 6F, 0F, 10F, 10F, 16F);

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final EnumProperty<Axis> AXIS = BlockStateProperties.AXIS;
	
	public static final BooleanProperty[] CHAINED = new BooleanProperty[] {
			BooleanProperty.create("chain_down"),
			BooleanProperty.create("chain_up"),
			BooleanProperty.create("chain_north"),
			BooleanProperty.create("chain_south"),
			BooleanProperty.create("chain_west"),
			BooleanProperty.create("chain_east")
	};
	
	public Block strippedBlock = null;

	public WoodPostBlock(QuarkModule module, Block parent, String prefix, boolean nether) {
		super(prefix + parent.getRegistryName().getPath().replace("_fence", "_post"), module, CreativeModeTab.TAB_BUILDING_BLOCKS, 
				Properties.copy(parent).sound(nether ? SoundType.STEM : SoundType.WOOD));
		
		BlockState state = stateDefinition.any().setValue(WATERLOGGED, false).setValue(AXIS, Axis.Y);
		for(BooleanProperty prop : CHAINED)
			state = state.setValue(prop, false);
		registerDefaultState(state);
		
		RenderLayerHandler.setRenderType(this, RenderTypeSkeleton.CUTOUT);
	}
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public BlockState getToolModifiedState(BlockState state, Level world, BlockPos pos, Player player, ItemStack stack, ToolType toolType) {
		if(strippedBlock == null || toolType != ToolType.AXE)
			return super.getToolModifiedState(state, world, pos, player, stack, toolType);
		
		BlockState newState = strippedBlock.defaultBlockState();
		for(Property p : state.getProperties())
			newState = newState.setValue(p, state.getValue(p));
		
		return newState;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		switch(state.getValue(AXIS)) {
		case X: return SHAPE_X;
		case Y: return SHAPE_Y;
		default: return SHAPE_Z;
		}
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return !state.getValue(WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return getState(context.getLevel(), context.getClickedPos(), context.getClickedFace().getAxis());
	}
	
	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
		
		BlockState newState = getState(worldIn, pos, state.getValue(AXIS));
		if(!newState.equals(state))
			worldIn.setBlockAndUpdate(pos, newState);
	}
	
	private BlockState getState(Level world, BlockPos pos, Axis axis) {
		BlockState state = defaultBlockState().setValue(WATERLOGGED, world.getFluidState(pos).getType() == Fluids.WATER).setValue(AXIS, axis);
		
		for(Direction d : Direction.values()) {
			if(d.getAxis() == axis)
				continue;

			BlockState sideState = world.getBlockState(pos.relative(d));
			if((sideState.getBlock() instanceof ChainBlock && sideState.getValue(BlockStateProperties.AXIS) == d.getAxis()) 
					|| (d == Direction.DOWN && sideState.getBlock() instanceof Lantern && sideState.getValue(Lantern.HANGING))) {
				BooleanProperty prop = CHAINED[d.ordinal()];
				state = state.setValue(prop, true);
			}
		}
		
		return state;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED, AXIS);
		for(BooleanProperty prop : CHAINED)
			builder.add(prop);
	}

}
