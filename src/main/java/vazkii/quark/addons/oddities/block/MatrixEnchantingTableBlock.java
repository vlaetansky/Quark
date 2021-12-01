package vazkii.quark.addons.oddities.block;

import java.util.Random;
import java.util.function.BooleanSupplier;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.addons.oddities.module.MatrixEnchantingModule;
import vazkii.quark.addons.oddities.tile.MatrixEnchantingTableTileEntity;
import vazkii.quark.api.IEnchantmentInfluencer;
import vazkii.quark.api.IModifiableEnchantmentInfluencer;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.QuarkModule;

public class MatrixEnchantingTableBlock extends EnchantmentTableBlock implements IQuarkBlock {

	private final QuarkModule module;
	private BooleanSupplier enabledSupplier = () -> true;
	
	public MatrixEnchantingTableBlock(QuarkModule module) {
		super(Block.Properties.copy(Blocks.ENCHANTING_TABLE));
		
		this.module = module;
		RegistryHelper.registerBlock(this, "matrix_enchanter");
		RegistryHelper.setCreativeTab(this, CreativeModeTab.TAB_DECORATIONS);
	}

	@Override
	public MutableComponent getName() {
		return Blocks.ENCHANTING_TABLE.getName();
	}
	
	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		if(isEnabled() || group == CreativeModeTab.TAB_SEARCH)
			super.fillItemCategory(group, items);
	}

	@Override
	public MatrixEnchantingTableBlock setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

	@Nullable
	@Override
	public QuarkModule getModule() {
		return module;
	}

	@Override
	public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
		return new MatrixEnchantingTableTileEntity();
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult raytrace) {
		if(!(worldIn.getBlockEntity(pos) instanceof MatrixEnchantingTableTileEntity))
			worldIn.setBlockEntity(pos, createTileEntity(state, worldIn));

		if(ModuleLoader.INSTANCE.isModuleEnabled(MatrixEnchantingModule.class)) {
			if(player instanceof ServerPlayer)
				NetworkHooks.openGui((ServerPlayer) player, (MatrixEnchantingTableTileEntity) worldIn.getBlockEntity(pos), pos);
		} else
			worldIn.setBlockAndUpdate(pos, Blocks.ENCHANTING_TABLE.defaultBlockState());

		return InteractionResult.SUCCESS;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {
		boolean enabled = ModuleLoader.INSTANCE.isModuleEnabled(MatrixEnchantingModule.class);
		boolean showInfluences = enabled && MatrixEnchantingModule.allowInfluencing;
		boolean allowUnderwater = enabled && MatrixEnchantingModule.allowUnderwaterEnchanting;
		
		for(int i = -2; i <= 2; ++i)
			for(int j = -2; j <= 2; ++j) {
				if(i > -2 && i < 2 && j == -1)
					j = 2;

				if(rand.nextInt(16) == 0)
					for(int k = 0; k <= 1; ++k) {
						BlockPos blockpos = pos.offset(i, k, j);
						BlockState state = worldIn.getBlockState(blockpos); 
						if(state.getEnchantPowerBonus(worldIn, blockpos) > 0) {
							BlockPos test = pos.offset(i / 2, 0, j / 2);
							if(!(worldIn.isEmptyBlock(test) || (allowUnderwater && worldIn.getBlockState(test).getBlock() == Blocks.WATER)))
								break;
							
							if(showInfluences && state.getBlock() instanceof IEnchantmentInfluencer) {
							    IEnchantmentInfluencer influencer = (IEnchantmentInfluencer) state.getBlock();
								DyeColor color = influencer.getEnchantmentInfluenceColor(worldIn, blockpos, state);
								
								if(color != null) {
									float[] comp = color.getTextureDiffuseColors();

                                    if(influencer instanceof IModifiableEnchantmentInfluencer) {
                                        IModifiableEnchantmentInfluencer modifiableInfluencer = (IModifiableEnchantmentInfluencer) influencer;
                                        comp = modifiableInfluencer.getModifiedColorComponents(worldIn, blockpos, state, comp);
                                    }

                                    int steps = 20;
									double dx = (double) (pos.getX() - blockpos.getX()) / steps;
									double dy = (double) (pos.getY() - blockpos.getY()) / steps;
									double dz = (double) (pos.getZ() - blockpos.getZ()) / steps;

									for(int p = 0; p < steps; p++) {
										if(rand.nextDouble() < 0.5)
											continue;
										
										double px = blockpos.getX() + 0.5 + dx * p + rand.nextDouble() * 0.2 - 0.1;
										double py = blockpos.getY() + 0.5 + dy * p + Math.sin((double) p / steps * Math.PI) * 0.5 + rand.nextDouble() * 0.2 - 0.1;
										double pz = blockpos.getZ() + 0.5 + dz * p + rand.nextDouble() * 0.2 - 0.1;
										
										worldIn.addParticle(new DustParticleOptions(comp[0], comp[1], comp[2], 1F), px, py, pz, 0, 0, 0);
									}
								}
							}

							worldIn.addParticle(ParticleTypes.ENCHANT, pos.getX() + 0.5, pos.getY() + 2.0, pos.getZ() + 0.5, i + rand.nextFloat() - 0.5, k - rand.nextFloat() - 1.0, j + rand.nextFloat() - 0.5);
						}
					}
			}
	}

	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(worldIn, pos, state, placer, stack);

		if(stack.hasCustomHoverName()) {
			BlockEntity tileentity = worldIn.getBlockEntity(pos);

			if(tileentity instanceof MatrixEnchantingTableTileEntity)
				((MatrixEnchantingTableTileEntity) tileentity).setCustomName(stack.getHoverName());
		}
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		BlockEntity tileentity = worldIn.getBlockEntity(pos);

		if(tileentity instanceof MatrixEnchantingTableTileEntity) {
			MatrixEnchantingTableTileEntity enchanter = (MatrixEnchantingTableTileEntity) tileentity;
			enchanter.dropItem(0);
			enchanter.dropItem(1);
		}

		super.onRemove(state, worldIn, pos, newState, isMoving);
	}

}
