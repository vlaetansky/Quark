package vazkii.quark.content.building.block;

import java.util.function.BooleanSupplier;

import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.building.block.be.VariantFurnaceBlockEntity;
import vazkii.quark.content.building.module.VariantFurnacesModule;

import javax.annotation.Nonnull;

public class VariantFurnaceBlock extends FurnaceBlock implements IQuarkBlock {

	private final QuarkModule module;

	public VariantFurnaceBlock(String type, QuarkModule module, Properties props) {
		super(props);

		RegistryHelper.registerBlock(this, type + "_furnace");
		RegistryHelper.setCreativeTab(this, CreativeModeTab.TAB_DECORATIONS);

		this.module = module;
	}

	@Override
	public BlockEntity newBlockEntity(@Nonnull BlockPos p_153277_, @Nonnull BlockState p_153278_) {
		return new VariantFurnaceBlockEntity(p_153277_, p_153278_);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level p_153273_, @Nonnull BlockState p_153274_, @Nonnull BlockEntityType<T> p_153275_) {
		return createFurnaceTicker(p_153273_, p_153275_, VariantFurnacesModule.blockEntityType);
	}

	@Override
	protected void openContainer(Level p_53631_, @Nonnull BlockPos p_53632_, @Nonnull Player p_53633_) {
		BlockEntity blockentity = p_53631_.getBlockEntity(p_53632_);
		if(blockentity instanceof AbstractFurnaceBlockEntity) {
			p_53633_.openMenu((MenuProvider) blockentity);
			p_53633_.awardStat(Stats.INTERACT_WITH_FURNACE);
		}
	}

	@Override
	public QuarkModule getModule() {
		return module;
	}

	@Override
	public IQuarkBlock setCondition(BooleanSupplier condition) {
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return true;
	}

}
