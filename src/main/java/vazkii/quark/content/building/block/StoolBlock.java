package vazkii.quark.content.building.block;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.building.entity.StoolEntity;
import vazkii.quark.content.building.module.StoolsModule;

public class StoolBlock extends QuarkBlock implements SimpleWaterloggedBlock {

	private static final VoxelShape SHAPE_TOP = Block.box(0F, 1F, 0F, 16F, 9F, 16F);
	private static final VoxelShape SHAPE_LEG = Block.box(0F, 0F, 0F, 4F, 1F, 4F);

	private static final VoxelShape SHAPE_TOP_BIG = Block.box(0F, 8F, 0F, 16F, 16F, 16F);
	private static final VoxelShape SHAPE_LEG_BIG = Block.box(0F, 0F, 0F, 4F, 8F, 4F);

	private static final VoxelShape SHAPE = Shapes.or(SHAPE_TOP, SHAPE_LEG,
			SHAPE_LEG.move(0.75F, 0F, 0F),
			SHAPE_LEG.move(0.75F, 0F, 0.75F),
			SHAPE_LEG.move(0F, 0F, 0.75F));

	private static final VoxelShape SHAPE_BIG = Shapes.or(SHAPE_TOP_BIG, SHAPE_LEG_BIG,
			SHAPE_LEG_BIG.move(0.75F, 0F, 0F),
			SHAPE_LEG_BIG.move(0.75F, 0F, 0.75F),
			SHAPE_LEG_BIG.move(0F, 0F, 0.75F));

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final BooleanProperty BIG = BooleanProperty.create("big");
	public static final BooleanProperty SAT_IN = BooleanProperty.create("sat_in");

	public StoolBlock(QuarkModule module, DyeColor color) {
		super(color.getName() + "_stool", module, CreativeModeTab.TAB_DECORATIONS, 
				BlockBehaviour.Properties.of(Material.WOOL, color.getMaterialColor())
				.sound(SoundType.WOOD)
				.strength(0.2F)
				.noOcclusion());

		registerDefaultState(stateDefinition.any().setValue(WATERLOGGED, false).setValue(BIG, false).setValue(SAT_IN, false));
	}
	
	public void blockClicked(Level world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		if(!state.getValue(BIG)) {
			world.setBlockAndUpdate(pos, state.setValue(BIG, true));
	         world.scheduleTick(pos, this, 1);
		}
	}
	
	@Override
	public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand) {
		fixState(worldIn, pos, state);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		if(state.getValue(SAT_IN) || !worldIn.getBlockState(pos.above()).isAir() || player.getVehicle() != null)
			return super.use(state, worldIn, pos, player, handIn, hit);

		if(!worldIn.isClientSide) {
			StoolEntity entity = new StoolEntity(StoolsModule.stoolEntity, worldIn);
			entity.setPos(pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5);
			
			worldIn.addFreshEntity(entity);
			player.startRiding(entity);
			
			worldIn.setBlockAndUpdate(pos, state.setValue(SAT_IN, true));
		}
		 
		return InteractionResult.SUCCESS;
	}

	@Override
	public void fallOn(Level worldIn, BlockState state, BlockPos pos, Entity entityIn, float fallDistance) {
		super.fallOn(worldIn, state, pos, entityIn, fallDistance * 0.5F);
	}
	
	@Override
	public void updateEntityAfterFallOn(BlockGetter worldIn, Entity entityIn) {
		if(entityIn.isSuppressingBounce())
			super.updateEntityAfterFallOn(worldIn, entityIn);
		else
			this.bounceEntity(entityIn);
	}

	private void bounceEntity(Entity entity) {
		Vec3 vector3d = entity.getDeltaMovement();
		if(vector3d.y < 0.0D) {
			double d0 = entity instanceof LivingEntity ? 1.0D : 0.8D;
			entity.setDeltaMovement(vector3d.x, -vector3d.y * (double)0.66F * d0, vector3d.z);
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return state.getValue(BIG) ? SHAPE_BIG : SHAPE;
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
		return getStateFor(context.getLevel(), context.getClickedPos());
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);

		fixState(worldIn, pos, state);
	}
	
	private void fixState(Level worldIn, BlockPos pos, BlockState state) {
		BlockState target = getStateFor(worldIn, pos);
		if(!target.equals(state))
			worldIn.setBlockAndUpdate(pos, target);
	}

	private BlockState getStateFor(Level world, BlockPos pos) {
		return defaultBlockState()
				.setValue(WATERLOGGED, world.getFluidState(pos).getType() == Fluids.WATER)
				.setValue(BIG, world.getBlockState(pos.above()).getShape(world, pos.above()).min(Axis.Y) == 0)
				.setValue(SAT_IN, world.getEntitiesOfClass(StoolEntity.class, new AABB(pos, pos.above()).inflate(0.4), e -> e.blockPosition().equals(pos)).size() > 0);
	}
	
	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}
	
	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
		return blockState.getValue(SAT_IN) ? 15 : 0;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED, BIG, SAT_IN);
	}

}
