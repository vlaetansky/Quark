package vazkii.quark.content.tweaks.module;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.tags.Tag;
import net.minecraft.tags.ItemTags;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.client.player.Input;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = ModuleCategory.TWEAKS, hasSubscriptions = true)
public class EnhancedLaddersModule extends QuarkModule {

	@Config.Max(0)
	@Config
    public double fallSpeed = -0.2;

	private static Tag<Item> laddersTag;
	
	@Override
	public void setup() {
		laddersTag = ItemTags.createOptional(new ResourceLocation(Quark.MOD_ID, "ladders"));
	}
	
	private static boolean canAttachTo(BlockState state, Block ladder, LevelReader world, BlockPos pos, Direction facing) {
		if (ladder instanceof LadderBlock) {
			BlockPos offset = pos.relative(facing);
			BlockState blockstate = world.getBlockState(offset);
			return !blockstate.isSignalSource() && blockstate.isFaceSturdy(world, offset, facing); 
		}

		return false;
	}

	@SubscribeEvent
	public void onInteract(PlayerInteractEvent.RightClickBlock event) {
		Player player = event.getPlayer();
		InteractionHand hand = event.getHand();
		ItemStack stack = player.getItemInHand(hand);

		if(!stack.isEmpty() && stack.getItem().is(laddersTag)) {
			Block block = Block.byItem(stack.getItem());
			Level world = event.getWorld();
			BlockPos pos = event.getPos();
			while(world.getBlockState(pos).getBlock() == block) {
				event.setCanceled(true);
				BlockPos posDown = pos.below();

				if(Level.isOutsideBuildHeight(posDown))
					break;

				BlockState stateDown = world.getBlockState(posDown);

				if(stateDown.getBlock() == block)
					pos = posDown;
				else {
					boolean water = stateDown.getBlock() == Blocks.WATER;
					if(water || stateDown.getBlock().isAir(stateDown, world, posDown)) {
						BlockState copyState = world.getBlockState(pos);

						Direction facing = copyState.getValue(LadderBlock.FACING);
						if(canAttachTo(copyState, block, world, posDown, facing.getOpposite())) {
							world.setBlockAndUpdate(posDown, copyState.setValue(BlockStateProperties.WATERLOGGED, water));
							world.playSound(null, posDown.getX(), posDown.getY(), posDown.getZ(), SoundEvents.LADDER_PLACE, SoundSource.BLOCKS, 1F, 1F);

							if(world.isClientSide)
								player.swing(hand);

							if(!player.isCreative()) {
								stack.shrink(1);

								if(stack.getCount() <= 0)
									player.setItemInHand(hand, ItemStack.EMPTY);
							}
						}
					}
					break;
				} 
			}
		}
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if(event.phase == TickEvent.Phase.START) {
			Player player = event.player;
			if(player.onClimbable() && player.level.isClientSide) {
				BlockPos playerPos = player.blockPosition();
				BlockPos downPos = playerPos.below();
				
				boolean scaffold = player.level.getBlockState(playerPos).getBlock() == Blocks.SCAFFOLDING;
				if(player.isCrouching() == scaffold &&
						player.zza == 0 &&
						player.yya <= 0 &&
						player.xxa == 0 &&
						player.xRot > 70 &&
						!player.jumping &&
						!player.abilities.flying &&
						player.level.getBlockState(downPos).isLadder(player.level, downPos, player)) {
					Vec3 move = new Vec3(0, fallSpeed, 0);
					player.setBoundingBox(player.getBoundingBox().move(move));						
					player.move(MoverType.SELF, Vec3.ZERO);
				}
			}
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onInput(InputUpdateEvent event) {
		Player player = event.getPlayer();
		if(player.onClimbable() && player.level.getBlockState(player.blockPosition()).getBlock() != Blocks.SCAFFOLDING
				&& Minecraft.getInstance().screen != null && !(player.zza == 0 && player.xRot > 70)) {
			Input input = event.getMovementInput();
			if(input != null)
				input.shiftKeyDown = true; // sneaking
		}
	}

}
