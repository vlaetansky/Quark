package vazkii.quark.content.building.block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements.Type;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.handler.RenderLayerHandler.RenderTypeSkeleton;
import vazkii.quark.base.module.QuarkModule;

public class GrateBlock extends QuarkBlock implements SimpleWaterloggedBlock {
	private static final VoxelShape TRUE_SHAPE = box(0, 15, 0, 16, 16, 16);
	private static final Float2ObjectArrayMap<VoxelShape> WALK_BLOCK_CACHE = new Float2ObjectArrayMap<>();

	public static BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public GrateBlock(QuarkModule module) {
		super("grate", module, CreativeModeTab.TAB_DECORATIONS,
				Block.Properties.of(Material.METAL)
						.strength(5, 10)
						.sound(SoundType.METAL)
						.noOcclusion());

		registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
		RenderLayerHandler.setRenderType(this, RenderTypeSkeleton.CUTOUT);
	}

	private static VoxelShape createNewBox(double stepHeight) {
		return box(0, 15, 0, 16, 17 + 16 * stepHeight, 16);
	}

	@Override
	public boolean hasDynamicShape() {
		return true;
	}

	@Nonnull
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return TRUE_SHAPE;
	}

	private static VoxelShape getCachedShape(float stepHeight) {
		return WALK_BLOCK_CACHE.computeIfAbsent(stepHeight, GrateBlock::createNewBox);
	}

	@Nonnull
	@Override
	public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, CollisionContext context) {
		Entity entity = context instanceof EntityCollisionContext ? ((EntityCollisionContext) context).getEntity() : null;

		if (entity != null) {
			if (entity instanceof ItemEntity || entity instanceof ExperienceOrb)
				return Shapes.empty();

			boolean animal = entity instanceof Animal;
			boolean leashed = animal && ((Animal) entity).getLeashHolder() != null;
			boolean onGrate = world.getBlockState(entity.blockPosition().offset(0, -1, 0)).getBlock() instanceof GrateBlock;

			if (animal && !leashed && !onGrate) {
				return getCachedShape(entity.maxUpStep);
			}

			return TRUE_SHAPE;
		}

		return TRUE_SHAPE;
	}

	@Nullable
	@Override
	public BlockPathTypes getAiPathNodeType(BlockState state, BlockGetter world, BlockPos pos, @Nullable Mob entity) {
		if (entity instanceof Animal)
			return BlockPathTypes.DAMAGE_OTHER;
		return null;
	}

	@Nonnull
	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
	}

	@Override
	public boolean isPathfindable(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, PathComputationType path) {
		return false;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos) {
		return !state.getValue(WATERLOGGED);
	}

//	@Override TODO alternative?
//	public boolean canCreatureSpawn(BlockState state, BlockGetter world, BlockPos pos, Type type, EntityType<?> entityType) {
//		return false;
//	}

	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED);
	}
}
