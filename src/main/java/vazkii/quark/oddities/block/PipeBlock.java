package vazkii.quark.oddities.block;

import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.items.CapabilityItemHandler;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.handler.RenderLayerHandler.RenderTypeSkeleton;
import vazkii.quark.base.module.Module;
import vazkii.quark.oddities.tile.PipeTileEntity;

public class PipeBlock extends QuarkBlock implements IWaterLoggable {

	private static final VoxelShape CENTER_SHAPE = VoxelShapes.create(0.3125, 0.3125, 0.3125, 0.6875, 0.6875, 0.6875);

	private static final VoxelShape DOWN_SHAPE = VoxelShapes.create(0.3125, 0, 0.3125, 0.6875, 0.6875, 0.6875);
	private static final VoxelShape UP_SHAPE = VoxelShapes.create(0.3125, 0.3125, 0.3125, 0.6875, 1, 0.6875);
	private static final VoxelShape NORTH_SHAPE = VoxelShapes.create(0.3125, 0.3125, 0, 0.6875, 0.6875, 0.6875);
	private static final VoxelShape SOUTH_SHAPE = VoxelShapes.create(0.3125, 0.3125, 0.3125, 0.6875, 0.6875, 1);
	private static final VoxelShape WEST_SHAPE = VoxelShapes.create(0, 0.3125, 0.3125, 0.6875, 0.6875, 0.6875);
	private static final VoxelShape EAST_SHAPE = VoxelShapes.create(0.3125, 0.3125, 0.3125, 1, 0.6875, 0.6875);

//	private static final VoxelShape DOWN_FLARE_SHAPE = VoxelShapes.create(0.25, 0.25, 0.25, 0.75, 0.325, 0.75);
//	private static final VoxelShape UP_FLARE_SHAPE = VoxelShapes.create(0.25, 0.625, 0.25, 0.75, 0.75, 0.75);
//	private static final VoxelShape NORTH_FLARE_SHAPE = VoxelShapes.create(0.25, 0.25, 0.25, 0.75, 0.75, 0.325);
//	private static final VoxelShape SOUTH_FLARE_SHAPE = VoxelShapes.create(0.25, 0.25, 0.625, 0.75, 0.75, 0.75);
//	private static final VoxelShape WEST_FLARE_SHAPE = VoxelShapes.create(0.25, 0.25, 0.25, 0.325, 0.75, 0.75);
//	private static final VoxelShape EAST_FLARE_SHAPE = VoxelShapes.create(0.625, 0.25, 0.25, 0.75, 0.75, 0.75);
//
//	private static final VoxelShape DOWN_TERMINAL_SHAPE = VoxelShapes.create(0.25, 0, 0.25, 0.75, 0.125, 0.75);
//	private static final VoxelShape UP_TERMINAL_SHAPE = VoxelShapes.create(0.25, 0.875, 0.25, 0.75, 1, 0.75);
//	private static final VoxelShape NORTH_TERMINAL_SHAPE = VoxelShapes.create(0.25, 0.25, 0, 0.75, 0.75, 0.125);
//	private static final VoxelShape SOUTH_TERMINAL_SHAPE = VoxelShapes.create(0.25, 0.25, 0.875, 0.75, 0.75, 1);
//	private static final VoxelShape WEST_TERMINAL_SHAPE = VoxelShapes.create(0, 0.25, 0.25, 0.125, 0.75, 0.75);
//	private static final VoxelShape EAST_TERMINAL_SHAPE = VoxelShapes.create(0.875, 0.25, 0.25, 1, 0.75, 0.75);

	public static final EnumProperty<ConnectionType> DOWN = EnumProperty.create("down", ConnectionType.class);
	public static final EnumProperty<ConnectionType> UP = EnumProperty.create("up", ConnectionType.class);
	public static final EnumProperty<ConnectionType> NORTH = EnumProperty.create("north", ConnectionType.class);
	public static final EnumProperty<ConnectionType> SOUTH = EnumProperty.create("south", ConnectionType.class);
	public static final EnumProperty<ConnectionType> WEST = EnumProperty.create("west", ConnectionType.class);
	public static final EnumProperty<ConnectionType> EAST = EnumProperty.create("east", ConnectionType.class);
	public static final BooleanProperty ENABLED = BooleanProperty.create("enabled");
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	@SuppressWarnings("unchecked")
	private static final EnumProperty<ConnectionType>[] CONNECTIONS = new EnumProperty[] {
			DOWN, UP, NORTH, SOUTH, WEST, EAST
	};

	private static final VoxelShape[] SIDE_BOXES = new VoxelShape[] {
			DOWN_SHAPE, UP_SHAPE, NORTH_SHAPE, SOUTH_SHAPE, WEST_SHAPE, EAST_SHAPE
	};
	
//	private static final VoxelShape[] FLARE_BOXES = new VoxelShape[] {
//			DOWN_FLARE_SHAPE, UP_FLARE_SHAPE, NORTH_FLARE_SHAPE,
//			SOUTH_FLARE_SHAPE, WEST_FLARE_SHAPE, EAST_FLARE_SHAPE
//	};
//
//	private static final VoxelShape[] TERMINAL_BOXES = new VoxelShape[] {
//			DOWN_TERMINAL_SHAPE, UP_TERMINAL_SHAPE, NORTH_TERMINAL_SHAPE,
//			SOUTH_TERMINAL_SHAPE, WEST_TERMINAL_SHAPE, EAST_TERMINAL_SHAPE
//	};

	private static final VoxelShape[] shapeCache = new VoxelShape[64];
	private static final Map<BlockState, Direction> FLARE_STATES = Maps.newHashMap();

	public PipeBlock(Module module) {
		super("pipe", module, ItemGroup.REDSTONE, 
				Block.Properties.create(Material.GLASS)
				.hardnessAndResistance(3F, 10F)
				.sound(SoundType.GLASS)
				.notSolid());
		
		setDefaultState(getDefaultState()
				.with(DOWN, ConnectionType.NONE).with(UP, ConnectionType.NONE)
				.with(NORTH, ConnectionType.NONE).with(SOUTH, ConnectionType.NONE)
				.with(WEST, ConnectionType.NONE).with(EAST, ConnectionType.NONE)
				.with(ENABLED, true)
				.with(WATERLOGGED, false));

		stateLoop: for (BlockState state : stateContainer.getValidStates()) {
			Direction onlySide = null;

			for (Direction facing : Direction.values()) {
				if (state.get(CONNECTIONS[facing.getIndex()]) != ConnectionType.NONE) {
					if (onlySide == null)
						onlySide = facing;
					else
						continue stateLoop;
				}
			}

			if (onlySide != null)
				FLARE_STATES.put(state, onlySide.getOpposite());
		}
		
		RenderLayerHandler.setRenderType(this, RenderTypeSkeleton.CUTOUT);
	}
	
	@Override
	public boolean isToolEffective(BlockState state, ToolType tool) {
		return tool == ToolType.PICKAXE;
	}
	
	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		BlockState targetState = getTargetState(worldIn, pos, state.get(WATERLOGGED));
		if(!targetState.equals(state))
			worldIn.setBlockState(pos, targetState, 2 | 4);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getTargetState(context.getWorld(), context.getPos(), context.getWorld().getFluidState(context.getPos()).getFluid() == Fluids.WATER);
	}
	
	private BlockState getTargetState(World worldIn, BlockPos pos, boolean waterlog) {
		BlockState newState = getDefaultState();
		newState = newState.with(ENABLED, !worldIn.isBlockPowered(pos)).with(WATERLOGGED, waterlog);
		
		for(Direction facing : Direction.values()) {
			EnumProperty<ConnectionType> prop = CONNECTIONS[facing.ordinal()];
			ConnectionType type = getConnectionTo(worldIn, pos, facing);

			newState = newState.with(prop, type);
		}
		
		return newState;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		int index = 0;
		for(Direction dir : Direction.values()) {
			int ord = dir.ordinal();
			if(state.get(CONNECTIONS[ord]).isSolid)
				index += (1 << ord);
		}
		
		VoxelShape cached = shapeCache[index];
		if(cached == null) {
			VoxelShape currShape = CENTER_SHAPE;
			
			for(Direction dir : Direction.values()) {
				ConnectionType type = getType(state, dir);
				if(type != null && type.isSolid)
					currShape = VoxelShapes.or(currShape, SIDE_BOXES[dir.ordinal()]);
//				if(type == null || type.isFlared)
//					currShape = VoxelShapes.or(currShape, (type == ConnectionType.TERMINAL ? TERMINAL_BOXES : FLARE_BOXES)[dir.ordinal()]);
			}
			
			shapeCache[index] = currShape;
			cached = currShape;
		}
		
		return cached;
	}

	public static ConnectionType getType(BlockState state, Direction side) {
		if(FLARE_STATES.containsKey(state) && FLARE_STATES.get(state) == side)
			return null;
		
		EnumProperty<ConnectionType> prop = CONNECTIONS[side.ordinal()];
		return state.get(prop);
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(UP, DOWN, NORTH, SOUTH, WEST, EAST, ENABLED, WATERLOGGED);
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile instanceof PipeTileEntity)
			return ((PipeTileEntity) tile).getComparatorOutput();
		return 0;
	}
	
	@Override
	public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
		TileEntity tileentity = worldIn.getTileEntity(pos);

		if(tileentity instanceof PipeTileEntity)
			((PipeTileEntity) tileentity).dropAllItems();

		super.onPlayerDestroy(worldIn, pos, state);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new PipeTileEntity();	
	}

	private ConnectionType getConnectionTo(IBlockReader world, BlockPos pos, Direction face) {
		pos = pos.offset(face);
//		TileEntity tile = world instanceof ChunkCache ? ((ChunkCache) world).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) : world.getTileEntity(pos);
		TileEntity tile = world.getTileEntity(pos);
		
		if(tile != null) {
			if(tile instanceof PipeTileEntity)
				return ConnectionType.PIPE;
			else if(tile instanceof IInventory || tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite()).isPresent())
				return ConnectionType.TERMINAL;
		}

		BlockState stateAt = world.getBlockState(pos);
		Block blockAt = stateAt.getBlock();
		if((face.getAxis() == Axis.Y && blockAt.isIn(BlockTags.WALLS))
				|| ((blockAt instanceof PistonBlock || blockAt instanceof PistonHeadBlock) && stateAt.get(DirectionalBlock.FACING) == face.getOpposite()))
				return ConnectionType.PROP;

		return ConnectionType.NONE;
	}

	public enum ConnectionType implements IStringSerializable {

		NONE(false, false, false),
		PIPE(true, true, false),
		TERMINAL(true, true, true),
		PROP(true, false, false);

		ConnectionType(boolean isSolid, boolean allowsItems, boolean isFlared) {
			this.isSolid = isSolid;
			this.allowsItems = allowsItems;
			this.isFlared = isFlared;
		}

		public final boolean isSolid, allowsItems, isFlared;

		@Override
		public String getString() { // getName
			return name().toLowerCase(Locale.ROOT);
		}

	}
	
}
