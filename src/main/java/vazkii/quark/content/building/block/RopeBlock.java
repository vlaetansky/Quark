package vazkii.quark.content.building.block;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import vazkii.arl.interf.IBlockItemProvider;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.handler.RenderLayerHandler.RenderTypeSkeleton;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.automation.module.PistonsMoveTileEntitiesModule;
import vazkii.quark.content.building.module.RopeModule;

public class RopeBlock extends QuarkBlock implements IBlockItemProvider {

	private static final VoxelShape SHAPE = box(6, 0, 6, 10, 16, 10);

	public RopeBlock(String regname, QuarkModule module, CreativeModeTab creativeTab, Properties properties) {
		super(regname, module, creativeTab, properties);
		
		RenderLayerHandler.setRenderType(this, RenderTypeSkeleton.CUTOUT);
	}
	
	@Override
	public BlockItem provideItemBlock(Block block, Item.Properties properties) {
		return new BlockItem(block, properties) {
			@Override
			public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
				return world.getBlockState(pos).getBlock() instanceof RopeBlock;
			}
		};
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}
	
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if(hand == InteractionHand.MAIN_HAND) {
			ItemStack stack = player.getItemInHand(hand);
			if(stack.getItem() == asItem() && !player.isDiscrete()) {
				if(pullDown(worldIn, pos)) {
					if(!player.isCreative())
						stack.shrink(1);
					
					worldIn.playSound(null, pos, soundType.getPlaceSound(), SoundSource.BLOCKS, 0.5F, 1F);
					return InteractionResult.SUCCESS;
				}
			} else if (stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
				return FluidUtil.interactWithFluidHandler(player, hand, worldIn, getBottomPos(worldIn, pos), Direction.UP) ? InteractionResult.SUCCESS : InteractionResult.PASS;
			} else if (stack.getItem() == Items.GLASS_BOTTLE) {
				BlockPos bottomPos = getBottomPos(worldIn, pos);
				BlockState stateAt = worldIn.getBlockState(bottomPos);
				if (stateAt.getMaterial() == Material.WATER) {
					Vec3 playerPos = player.position();
					worldIn.playSound(player, playerPos.x, playerPos.y, playerPos.z, SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
					stack.shrink(1);
					ItemStack bottleStack = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
					player.awardStat(Stats.ITEM_USED.get(stack.getItem()));

					if (stack.isEmpty())
						player.setItemInHand(hand, bottleStack);
					else if (!player.getInventory().add(bottleStack))
						player.drop(bottleStack, false);


					return InteractionResult.SUCCESS;
				}

				return InteractionResult.PASS;
			} else {
				if(pullUp(worldIn, pos)) {
					if(!player.isCreative()) {
						if(!player.addItem(new ItemStack(this)))
							player.drop(new ItemStack(this), false);
					}
					
					worldIn.playSound(null, pos, soundType.getBreakSound(), SoundSource.BLOCKS, 0.5F, 1F);
					return InteractionResult.SUCCESS;
				}
			}
		}
		
		return InteractionResult.PASS;
	}

	public boolean pullUp(Level world, BlockPos pos) {
		BlockPos basePos = pos;
		
		while(true) {
			pos = pos.below();
			BlockState state = world.getBlockState(pos);
			if(state.getBlock() != this)
				break;
		}
		
		BlockPos ropePos = pos.above();
		if(ropePos.equals(basePos))
			return false;

		world.setBlockAndUpdate(ropePos, Blocks.AIR.defaultBlockState());
		moveBlock(world, pos, ropePos);
		
		return true;
	}
	
	public boolean pullDown(Level world, BlockPos pos) {
		boolean can;
		boolean endRope = false;
		boolean wasAirAtEnd = false;
		
		do {
			pos = pos.below();
			if (!world.isInWorldBounds(pos))
				return false;

			BlockState state = world.getBlockState(pos);
			Block block = state.getBlock();
			
			if(block == this)
				continue;
			
			if(endRope) {
				can = wasAirAtEnd || world.isEmptyBlock(pos) || state.getMaterial().isReplaceable();
				break;
			}
			
			endRope = true;
			wasAirAtEnd = world.isEmptyBlock(pos);
		} while(true);
		
		if(can) {
			BlockPos ropePos = pos.above();
			moveBlock(world, ropePos, pos);
			
			BlockState ropePosState = world.getBlockState(ropePos);

			if(world.isEmptyBlock(ropePos) || ropePosState.getMaterial().isReplaceable()) {
				world.setBlockAndUpdate(ropePos, defaultBlockState());
				return true;
			}
		}
		
		return false;
	}

	private BlockPos getBottomPos(Level worldIn, BlockPos pos) {
		Block block = this;
		while (block == this) {
			pos = pos.below();
			BlockState state = worldIn.getBlockState(pos);
			block = state.getBlock();
		}

		return pos;

	}
	
	// mojang tag pls
	private boolean isIllegalBlock(Block block) {
		return block == Blocks.OBSIDIAN || block == Blocks.CRYING_OBSIDIAN || block == Blocks.RESPAWN_ANCHOR;
	}

	private void moveBlock(Level world, BlockPos srcPos, BlockPos dstPos) {
		BlockState state = world.getBlockState(srcPos);
		Block block = state.getBlock();
		
		if(state.getDestroySpeed(world, srcPos) == -1 || !state.canSurvive(world, dstPos) || state.isAir() ||
				state.getPistonPushReaction() != PushReaction.NORMAL || isIllegalBlock(block))
			return;
		
		BlockEntity tile = world.getBlockEntity(srcPos);
		if(tile != null) {
			if(RopeModule.forceEnableMoveTileEntities ? PistonsMoveTileEntitiesModule.shouldMoveTE(state) : PistonsMoveTileEntitiesModule.shouldMoveTE(true, state))
				return;

			tile.setRemoved();
		}
		
		FluidState fluidState = world.getFluidState(srcPos);
		world.setBlockAndUpdate(srcPos, fluidState.createLegacyBlock());
		
		BlockState nextState = Block.updateFromNeighbourShapes(state, world, dstPos);
		if(nextState.getProperties().contains(BlockStateProperties.WATERLOGGED))
			nextState = nextState.setValue(BlockStateProperties.WATERLOGGED, world.getFluidState(dstPos).getType() == Fluids.WATER);
		world.setBlockAndUpdate(dstPos, nextState);
		
		if(tile != null) {
			BlockEntity target = BlockEntity.loadStatic(dstPos, state, tile.saveWithFullMetadata());
			if (target != null) {
				world.setBlockEntity(target);
				target.setBlockState(state);
				target.setChanged();
			}
		}

		world.updateNeighborsAt(dstPos, state.getBlock());
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
		BlockPos upPos = pos.above();
		BlockState upState = worldIn.getBlockState(upPos);
		return upState.getBlock() == this || upState.isFaceSturdy(worldIn, upPos, Direction.DOWN);
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if(!state.canSurvive(worldIn, pos)) {
			worldIn.levelEvent(2001, pos, Block.getId(worldIn.getBlockState(pos)));
			dropResources(state, worldIn, pos);
			worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		}
	}

	@Override
	public boolean isLadder(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity) {
		return true;
	}

	@Nonnull
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 30;
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 60;
	}

}
