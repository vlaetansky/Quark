package vazkii.quark.content.building.block.be;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.quark.content.building.module.VariantChestsModule;

import javax.annotation.Nonnull;

public class VariantTrappedChestBlockEntity extends VariantChestBlockEntity {

	public VariantTrappedChestBlockEntity(BlockPos pos, BlockState state) {
		super(VariantChestsModule.trappedChestTEType, pos, state);
	}

	@Override
	protected void signalOpenCount(@Nonnull Level p_155865_, @Nonnull BlockPos p_155866_, @Nonnull BlockState p_155867_, int p_155868_, int p_155869_) {
		super.signalOpenCount(p_155865_, p_155866_, p_155867_, p_155868_, p_155869_);
		if (p_155868_ != p_155869_) {
			Block block = p_155867_.getBlock();
			p_155865_.updateNeighborsAt(p_155866_, block);
			p_155865_.updateNeighborsAt(p_155866_.below(), block);
		}
	}

}
