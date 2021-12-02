package vazkii.quark.content.mobs.item;

import javax.annotation.Nonnull;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.base.item.QuarkItem;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.mobs.entity.EnumStonelingVariant;
import vazkii.quark.content.mobs.entity.StonelingEntity;
import vazkii.quark.content.mobs.module.StonelingsModule;

public class DiamondHeartItem extends QuarkItem {

	public DiamondHeartItem(String regname, QuarkModule module, Properties properties) {
		super(regname, module, properties);
	}

	@Nonnull
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		InteractionHand hand = context.getHand();
		Direction facing = context.getClickedFace();

		if (player != null) {
			BlockState stateAt = world.getBlockState(pos);
			ItemStack stack = player.getItemInHand(hand);

			if (player.mayUseItemAt(pos, facing, stack) && stateAt.getDestroySpeed(world, pos) != -1) {

				EnumStonelingVariant variant = null;
				for (EnumStonelingVariant possibleVariant : EnumStonelingVariant.values()) {
					if (possibleVariant.getBlocks().contains(stateAt.getBlock()))
						variant = possibleVariant;
				}

				if (variant != null) {
					if (!world.isClientSide && world instanceof ServerLevelAccessor) {
						world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
						world.levelEvent(2001, pos, Block.getId(stateAt));

						StonelingEntity stoneling = new StonelingEntity(StonelingsModule.stonelingType, world);
						stoneling.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
						stoneling.setPlayerMade(true);
						stoneling.setYRot(player.getYRot() + 180F);
						stoneling.finalizeSpawn((ServerLevelAccessor) world, world.getCurrentDifficultyAt(pos), MobSpawnType.STRUCTURE, variant, null);
						world.addFreshEntity(stoneling);
						
						if(player instanceof ServerPlayer)
							CriteriaTriggers.SUMMONED_ENTITY.trigger((ServerPlayer) player, stoneling);

						if (!player.getAbilities().instabuild)
							stack.shrink(1);
					}

					return InteractionResult.SUCCESS;
				}
			}
		}
		
		return InteractionResult.PASS;
	}

	@Nonnull
	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.UNCOMMON;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(ItemStack stack) {
		return true;
	}

}
