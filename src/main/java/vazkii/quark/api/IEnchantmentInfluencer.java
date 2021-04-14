package vazkii.quark.api;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import java.util.List;

/**
 * Implement on a Block to make it influence matrix enchanting
 */
public interface IEnchantmentInfluencer {

    default void modifyInfluencedEnchantments(IBlockReader world, BlockPos pos, BlockState state, ItemStack stack, List<Enchantment> influencedEnchants) {
    }

	@Nullable DyeColor getEnchantmentInfluenceColor(IBlockReader world, BlockPos pos, BlockState state);
	
	default int getInfluenceStack(IBlockReader world, BlockPos pos, BlockState state) {
		return 1;
	}
}
