package vazkii.quark.addons.oddities.block;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.fml.network.NetworkHooks;
import vazkii.quark.addons.oddities.tile.CrateTileEntity;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.QuarkModule;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class CrateBlock extends QuarkBlock {

	public static final BooleanProperty PROPERTY_OPEN = BlockStateProperties.OPEN;

	public CrateBlock(QuarkModule module) {
		super("crate", module, CreativeModeTab.TAB_DECORATIONS, Properties.copy(Blocks.BARREL));
		registerDefaultState(stateDefinition.any().setValue(PROPERTY_OPEN, false));
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		if(worldIn.isClientSide) {
			return InteractionResult.SUCCESS;
		} else {
			BlockEntity tileentity = worldIn.getBlockEntity(pos);
			if(tileentity instanceof CrateTileEntity) {
				if(player instanceof ServerPlayer)
					NetworkHooks.openGui((ServerPlayer) player, (CrateTileEntity) worldIn.getBlockEntity(pos), pos);

				PiglinAi.angerNearbyPiglins(player, true);
			}

			return InteractionResult.CONSUME;
		}
	}

	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if(stack.hasCustomHoverName()) {
			BlockEntity tileentity = worldIn.getBlockEntity(pos);
			if(tileentity instanceof CrateTileEntity)
				((CrateTileEntity) tileentity).setCustomName(stack.getHoverName());
		}
	}

	@Override
	public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand) {
		BlockEntity tileentity = worldIn.getBlockEntity(pos);
		if(tileentity instanceof CrateTileEntity)
			((CrateTileEntity)tileentity).crateTick();
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if(!state.is(newState.getBlock())) {
			BlockEntity tileentity = worldIn.getBlockEntity(pos);

			if(tileentity instanceof CrateTileEntity) {
				CrateTileEntity crate = (CrateTileEntity) tileentity;
				crate.spillTheTea();
			}
		}

		super.onRemove(state, worldIn, pos, newState, isMoving);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
		return new CrateTileEntity();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(PROPERTY_OPEN);
	}

}
