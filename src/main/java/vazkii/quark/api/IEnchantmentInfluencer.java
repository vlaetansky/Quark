package vazkii.quark.api;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Implement on a Block to make it influence matrix enchanting
 */
public interface IEnchantmentInfluencer {
	@Nullable DyeColor getEnchantmentInfluenceColor(BlockGetter world, BlockPos pos, BlockState state);
	
	default int getInfluenceStack(BlockGetter world, BlockPos pos, BlockState state) {
		return 1;
	}
}
