package vazkii.quark.base.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;
import vazkii.quark.base.module.QuarkModule;

import java.util.function.BooleanSupplier;

// Wrapper to allow vanilla blocks to be treated as quark blocks contextualized under a module
public record QuarkBlockWrapper(Block parent,
								QuarkModule module) implements IQuarkBlock {

	@Override
	public Block getBlock() {
		return parent;
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
		return false;
	}

	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
		return false;
	}

}
