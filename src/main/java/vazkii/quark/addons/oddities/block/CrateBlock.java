package vazkii.quark.addons.oddities.block;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import vazkii.quark.addons.oddities.block.be.CrateBlockEntity;
import vazkii.quark.addons.oddities.module.CrateModule;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.QuarkModule;

public class CrateBlock extends QuarkBlock implements EntityBlock {

	public static final BooleanProperty PROPERTY_OPEN = BlockStateProperties.OPEN;

	public CrateBlock(QuarkModule module) {
		super("crate", module, CreativeModeTab.TAB_DECORATIONS, Properties.copy(Blocks.BARREL));
		registerDefaultState(stateDefinition.any().setValue(PROPERTY_OPEN, false));
	}

	@Nonnull
	@Override
	public InteractionResult use(@Nonnull BlockState state, Level worldIn, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand handIn, @Nonnull BlockHitResult hit) {
		if(worldIn.isClientSide) {
			return InteractionResult.SUCCESS;
		} else {
			BlockEntity tileentity = worldIn.getBlockEntity(pos);
			if(tileentity instanceof CrateBlockEntity) {
				if(player instanceof ServerPlayer)
					NetworkHooks.openGui((ServerPlayer) player, (CrateBlockEntity) worldIn.getBlockEntity(pos), pos);

				PiglinAi.angerNearbyPiglins(player, true);
			}

			return InteractionResult.CONSUME;
		}
	}

	@Override
	public void setPlacedBy(@Nonnull Level worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if(stack.hasCustomHoverName()) {
			BlockEntity tileentity = worldIn.getBlockEntity(pos);
			if(tileentity instanceof CrateBlockEntity)
				((CrateBlockEntity) tileentity).setCustomName(stack.getHoverName());
		}
	}

	@Override
	public void tick(@Nonnull BlockState state, ServerLevel worldIn, @Nonnull BlockPos pos, @Nonnull Random rand) {
		BlockEntity tileentity = worldIn.getBlockEntity(pos);
		if(tileentity instanceof CrateBlockEntity)
			((CrateBlockEntity)tileentity).crateTick();
	}

	@Override
	public void onRemove(BlockState state, @Nonnull Level worldIn, @Nonnull BlockPos pos, BlockState newState, boolean isMoving) {
		if(!state.is(newState.getBlock())) {
			BlockEntity tileentity = worldIn.getBlockEntity(pos);

			if(tileentity instanceof CrateBlockEntity) {
				CrateBlockEntity crate = (CrateBlockEntity) tileentity;
				crate.spillTheTea();
			}
		}

		super.onRemove(state, worldIn, pos, newState, isMoving);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(PROPERTY_OPEN);
	}

	@Override
	public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
		return new CrateBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level world, @Nonnull BlockState state, @Nonnull BlockEntityType<T> type) {
		return createTickerHelper(type, CrateModule.blockEntityType, CrateBlockEntity::tick);
	}

}
