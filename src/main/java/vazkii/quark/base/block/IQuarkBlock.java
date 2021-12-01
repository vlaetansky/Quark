package vazkii.quark.base.block;

import java.util.function.BooleanSupplier;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.extensions.IForgeBlock;
import vazkii.quark.base.module.QuarkModule;

/**
 * @author WireSegal
 * Created at 1:14 PM on 9/19/19.
 */
public interface IQuarkBlock extends IForgeBlock {

    @Nullable
    QuarkModule getModule();

    IQuarkBlock setCondition(BooleanSupplier condition);

    boolean doesConditionApply();

    default Block getBlock() {
    	return (Block) this;
    }
    
    default boolean isEnabled() {
        QuarkModule module = getModule();
        return module != null && module.enabled && doesConditionApply();
    }

    @Override
    default int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        if (state.getValues().containsKey(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED))
            return 0;

        Material material = state.getMaterial();
        if (material == Material.WOOL)
            return 60;
        return state.getMaterial().isFlammable() ? 20 : 0;
    }

    @Override
    default int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        if (state.getValues().containsKey(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED))
            return 0;

        Material material = state.getMaterial();
        if (material == Material.WOOL)
            return 30;
        return state.getMaterial().isFlammable() ? 5 : 0;
    }
}
