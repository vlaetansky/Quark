package vazkii.quark.content.automation.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Predicate;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.api.IIndirectConnector;
import vazkii.quark.api.IPistonCallback;
import vazkii.quark.api.QuarkCapabilities;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = ModuleCategory.AUTOMATION, hasSubscriptions = true)
public class PistonsMoveTileEntitiesModule extends QuarkModule {

	private static final WeakHashMap<Level, Map<BlockPos, CompoundTag>> movements = new WeakHashMap<>();
	private static final WeakHashMap<Level, List<Pair<BlockPos, CompoundTag>>> delayedUpdates = new WeakHashMap<>();

	@Config
	public static boolean enableChestsMovingTogether = true;
	
	public static boolean staticEnabled;
	
	@Config
	public static List<String> renderBlacklist = Lists.newArrayList("psi:programmer", "botania:starfield");
	@Config
	public static List<String> movementBlacklist = Lists.newArrayList("minecraft:spawner", "integrateddynamics:cable", "randomthings:blockbreaker", "minecraft:ender_chest", "minecraft:enchanting_table", "minecraft:trapped_chest", "quark:spruce_trapped_chest", "quark:birch_trapped_chest", "quark:jungle_trapped_chest", "quark:acacia_trapped_chest", "quark:dark_oak_trapped_chest", "endergetic:bolloom_bud");
	@Config
	public static List<String> delayedUpdateList = Lists.newArrayList("minecraft:dispenser", "minecraft:dropper");

	@Override
	public void construct() {
		IIndirectConnector.INDIRECT_STICKY_BLOCKS.add(Pair.of(ChestConnection.PREDICATE, ChestConnection.INSTANCE));
	}
	
	@Override
	public void configChanged() {
		staticEnabled = enabled;
	}
	
	@SubscribeEvent
	public void onWorldTick(WorldTickEvent event) {
		if (!delayedUpdates.containsKey(event.world) || event.phase == Phase.START)
			return;

		List<Pair<BlockPos, CompoundTag>> delays = delayedUpdates.get(event.world);
		if (delays.isEmpty())
			return;

		for (Pair<BlockPos, CompoundTag> delay : delays) {
			BlockPos pos = delay.getLeft();
			BlockState state = event.world.getBlockState(pos);
			BlockEntity tile = BlockEntity.loadStatic(pos, state, delay.getRight());

			if(tile != null) {
				tile.setBlockState(state);
				tile.setChanged();
				
				event.world.setBlockEntity(tile);
			}
			
			event.world.updateNeighbourForOutputSignal(pos, state.getBlock());
		}

		delays.clear();
	}

	// This is called from injected code and subsequently flipped, so to make it move, we return false
	public static boolean shouldMoveTE(boolean te, BlockState state) {
		if (!ModuleLoader.INSTANCE.isModuleEnabled(PistonsMoveTileEntitiesModule.class))
			return te;

		return shouldMoveTE(state);
	}

	public static boolean shouldMoveTE(BlockState state) {
		// Jukeboxes that are playing can't be moved so the music can be stopped
		if (state.getValues().containsKey(JukeboxBlock.HAS_RECORD) && state.getValue(JukeboxBlock.HAS_RECORD))
			return true;

		if (state.getBlock() == Blocks.PISTON_HEAD)
			return true;

		ResourceLocation res = state.getBlock().getRegistryName();
		return res == null || PistonsMoveTileEntitiesModule.movementBlacklist.contains(res.toString()) || PistonsMoveTileEntitiesModule.movementBlacklist.contains(res.getNamespace());
	}

	public static void detachTileEntities(Level world, PistonStructureResolver helper, Direction facing, boolean extending) {
		if (!ModuleLoader.INSTANCE.isModuleEnabled(PistonsMoveTileEntitiesModule.class))
			return;

		if (!extending)
			facing = facing.getOpposite();

		List<BlockPos> moveList = helper.getToPush();

		for (BlockPos pos : moveList) {
			BlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof EntityBlock) {
				BlockEntity tile = world.getBlockEntity(pos);
				if (tile != null) {
					if (hasCallback(tile))
						getCallback(tile).onPistonMovementStarted();

					world.removeBlockEntity(pos);

					registerMovement(world, pos.relative(facing), tile);
				}
			}
		}
	}

	public static boolean setPistonBlock(Level world, BlockPos pos, BlockState state, int flags) {
		if (!ModuleLoader.INSTANCE.isModuleEnabled(PistonsMoveTileEntitiesModule.class)) {
			world.setBlock(pos, state, flags);
			return false;
		}

		if(!enableChestsMovingTogether && state.getValues().containsKey(ChestBlock.TYPE))
			state = state.setValue(ChestBlock.TYPE, ChestType.SINGLE);

		Block block = state.getBlock();
		BlockEntity entity = getAndClearMovement(world, pos);
		boolean destroyed = false;

		if (entity != null) {
			BlockState currState = world.getBlockState(pos);
			BlockEntity currEntity = world.getBlockEntity(pos);

			world.removeBlock(pos, false);
			if (!block.canSurvive(state, world, pos)) {
				world.setBlock(pos, state, flags);
				world.setBlockEntity(entity);
				Block.dropResources(state, world, pos, entity);
				world.removeBlock(pos, false);
				destroyed = true;
			}

			if (!destroyed) {
				world.setBlockAndUpdate(pos, currState);
				if(currEntity != null)
					world.setBlockEntity(currEntity);
			}
		}

		if (!destroyed) {
			world.setBlock(pos, state, flags);
			
			if (world.getBlockEntity(pos) != null)
				world.setBlock(pos, state, 0);

			if (entity != null && !world.isClientSide) {
				if (delayedUpdateList.contains(block.getRegistryName().toString()))
					registerDelayedUpdate(world, pos, entity);
				else {
					entity.setBlockState(state);
					entity.setChanged();
					
					world.setBlockEntity(entity);
				}
			}
			world.updateNeighborsAt(pos, block);
		}

		return false; // the value is popped, doesn't matter what we return
	}

	private static void registerMovement(Level world, BlockPos pos, BlockEntity tile) {
		if (!movements.containsKey(world))
			movements.put(world, new HashMap<>());

		movements.get(world).put(pos, tile.saveWithFullMetadata());
	}

	public static BlockEntity getMovement(Level world, BlockPos pos) {
		return getMovement(world, pos, false);
	}

	private static BlockEntity getMovement(Level world, BlockPos pos, boolean remove) {
		if (!movements.containsKey(world))
			return null;

		Map<BlockPos, CompoundTag> worldMovements = movements.get(world);
		if (!worldMovements.containsKey(pos))
			return null;

		CompoundTag ret = worldMovements.get(pos);
		if (remove)
			worldMovements.remove(pos);

		return BlockEntity.loadStatic(pos, world.getBlockState(pos), ret);
	}

	private static BlockEntity getAndClearMovement(Level world, BlockPos pos) {
		BlockEntity tile = getMovement(world, pos, true);

		if (tile != null) {
			if (hasCallback(tile))
				getCallback(tile).onPistonMovementFinished();

			tile.setLevel(world);
			tile.clearRemoved();
		}

		return tile;
	}

	private static void registerDelayedUpdate(Level world, BlockPos pos, BlockEntity tile) {
		if (!delayedUpdates.containsKey(world))
			delayedUpdates.put(world, new ArrayList<>());

		delayedUpdates.get(world).add(Pair.of(pos, tile.saveWithFullMetadata()));
	}

	private static boolean hasCallback(BlockEntity tile) {
		return tile.getCapability(QuarkCapabilities.PISTON_CALLBACK).isPresent();
	}

	private static IPistonCallback getCallback(BlockEntity tile) {
		return tile.getCapability(QuarkCapabilities.PISTON_CALLBACK).orElse(() -> {});
	}
	
	public static class ChestConnection implements IIndirectConnector {

		public static ChestConnection INSTANCE = new ChestConnection();
		public static Predicate<BlockState> PREDICATE = ChestConnection::isValidState;
		
		@Override
		public boolean isEnabled() {
			return enableChestsMovingTogether;
		}
		
		private static boolean isValidState(BlockState state) {
			if(!(state.getBlock() instanceof ChestBlock))
				return false;
			
			ChestType type = state.getValue(ChestBlock.TYPE);
			return type != ChestType.SINGLE;
		}
		
		@Override
		public boolean canConnectIndirectly(Level world, BlockPos ourPos, BlockPos sourcePos, BlockState ourState, BlockState sourceState) {
			ChestType ourType = ourState.getValue(ChestBlock.TYPE);
			
			Direction baseDirection = ourState.getValue(ChestBlock.FACING);
			Direction targetDirection = ourType == ChestType.LEFT ? baseDirection.getClockWise() : baseDirection.getCounterClockWise();
			
			BlockPos targetPos = ourPos.relative(targetDirection);
			
			return targetPos.equals(sourcePos);
		}
		
	}

}
