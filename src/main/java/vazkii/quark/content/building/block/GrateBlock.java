package vazkii.quark.content.building.block;

import it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.StringRepresentable;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.ForgeEventFactory;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.block.SimpleFluidloggedBlock;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.handler.RenderLayerHandler.RenderTypeSkeleton;
import vazkii.quark.base.module.QuarkModule;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public class GrateBlock extends QuarkBlock implements SimpleFluidloggedBlock {
	private static final VoxelShape TRUE_SHAPE = box(0, 15, 0, 16, 16, 16);
	private static final Float2ObjectArrayMap<VoxelShape> WALK_BLOCK_CACHE = new Float2ObjectArrayMap<>();

	public static final EnumProperty<GrateFluid> FLUID = EnumProperty.create("fluid", GrateFluid.class);

	public GrateBlock(QuarkModule module) {
		super("grate", module, CreativeModeTab.TAB_DECORATIONS,
				Block.Properties.of(Material.METAL)
						.strength(5, 10)
						.sound(SoundType.METAL)
						.noOcclusion());

		registerDefaultState(defaultBlockState().setValue(FLUID, GrateFluid.AIR));
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
	public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
		return TRUE_SHAPE;
	}

	private static VoxelShape getCachedShape(float stepHeight) {
		return WALK_BLOCK_CACHE.computeIfAbsent(stepHeight, GrateBlock::createNewBox);
	}

	@Nonnull
	@Override
	public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
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

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Fluid fluidAt = context.getLevel().getFluidState(context.getClickedPos()).getType();
		BlockState state = defaultBlockState();
		return acceptsFluid(fluidAt) ? withFluid(state, fluidAt) : state;
	}

	@Override
	public boolean isPathfindable(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull PathComputationType path) {
		return false;
	}

	@Override
	public boolean propagatesSkylightDown(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos) {
		return fluidContained(state) == Fluids.EMPTY;
	}

	@Override
	public boolean isValidSpawn(BlockState state, BlockGetter world, BlockPos pos, Type type, EntityType<?> entityType) {
		return false;
	}

	@Override
	public boolean useShapeForLightOcclusion(@Nonnull BlockState state) {
		return true;
	}

	@Override
	public void neighborChanged(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Block updatedBlock, @Nonnull BlockPos neighbor, boolean isMoving) {
		super.neighborChanged(state, level, pos, updatedBlock, neighbor, isMoving);
		if (!pos.below().equals(neighbor)) {
			BlockState neighborState = level.getBlockState(neighbor);
			if (neighborState.getFluidState().is(FluidTags.WATER) &&
					fluidContained(state).isSame(Fluids.LAVA)) {
				level.destroyBlock(pos, true);
				level.setBlock(pos, ForgeEventFactory.fireFluidPlaceBlockEvent(level, pos, neighbor, Blocks.OBSIDIAN.defaultBlockState()), 3);
				level.levelEvent(1501, pos, 0); // lava fizz
			}
		}
	}

	@Nonnull
	@Override
	public BlockState updateShape(@Nonnull BlockState state, @Nonnull Direction facing, @Nonnull BlockState facingState, @Nonnull LevelAccessor level, @Nonnull BlockPos pos, @Nonnull BlockPos facingPos) {
		Fluid fluid = fluidContained(state);
		if (fluid != Fluids.EMPTY)
			level.scheduleTick(pos, fluid, fluid.getTickDelay(level));

		return super.updateShape(state, facing, facingState, level, pos, facingPos);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FLUID);
	}

	@Nonnull
	@Override
	public FluidState getFluidState(@Nonnull BlockState state) {
		return fluidContained(state).defaultFluidState();
	}

	@Override
	public boolean acceptsFluid(@Nonnull Fluid fluid) {
		return fluid == Fluids.WATER || fluid == Fluids.LAVA;
	}

	@Nonnull
	@Override
	public BlockState withFluid(@Nonnull BlockState state, @Nonnull Fluid fluid) {
		return state.setValue(FLUID, GrateFluid.fromFluid(fluid));
	}

	@Nonnull
	@Override
	public Fluid fluidContained(@Nonnull BlockState state) {
		return state.getValue(FLUID).getFluid();
	}

	public enum GrateFluid implements StringRepresentable {
		AIR("air", () -> Fluids.EMPTY), WATER("water", () -> Fluids.WATER), LAVA("lava", () -> Fluids.LAVA);

		private final String name;
		private final Supplier<Fluid> fluidSupplier;

		GrateFluid(String name, Supplier<Fluid> fluid) {
			this.name = name;
			fluidSupplier = fluid;
		}

		public Fluid getFluid() {
			return fluidSupplier.get();
		}

		@Nonnull
		@Override
		public String getSerializedName() {
			return name;
		}

		public static GrateFluid fromFluid(Fluid fluid) {
			if (fluid == Fluids.WATER)
				return WATER;
			else if (fluid == Fluids.LAVA)
				return LAVA;
			return AIR;
		}
	}
}
