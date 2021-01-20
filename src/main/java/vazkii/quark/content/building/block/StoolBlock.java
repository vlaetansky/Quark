package vazkii.quark.content.building.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BedPart;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.QuarkModule;

public class StoolBlock extends QuarkBlock implements IWaterLoggable {

	private static final VoxelShape SHAPE_TOP = Block.makeCuboidShape(0F, 8F, 0F, 16F, 16F, 16F);
	private static final VoxelShape SHAPE_LEG = Block.makeCuboidShape(0F, 0F, 0F, 4F, 8F, 4F);
	
	private static final VoxelShape SHAPE = VoxelShapes.or(SHAPE_TOP, SHAPE_LEG,
			SHAPE_LEG.withOffset(0.75F, 0F, 0F),
			SHAPE_LEG.withOffset(0.75F, 0F, 0.75F),
			SHAPE_LEG.withOffset(0F, 0F, 0.75F));

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public StoolBlock(QuarkModule module, DyeColor color) {
		super(color.getTranslationKey() + "_stool", module, ItemGroup.BUILDING_BLOCKS, 
				AbstractBlock.Properties.create(Material.WOOL, color.getMapColor())
				.sound(SoundType.WOOD)
				.hardnessAndResistance(0.2F)
				.notSolid());
		
		setDefaultState(stateContainer.getBaseState().with(WATERLOGGED, false));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
		return !state.get(WATERLOGGED);
	}

	@Override
	@SuppressWarnings("deprecation")
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getDefaultState().with(WATERLOGGED, context.getWorld().getFluidState(context.getPos()).getFluid() == Fluids.WATER);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED);
	}

}
