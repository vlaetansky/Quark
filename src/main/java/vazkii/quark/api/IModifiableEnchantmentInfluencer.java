package vazkii.quark.api;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface IModifiableEnchantmentInfluencer extends IEnchantmentInfluencer {
	List<Enchantment> getModifiedEnchantments(BlockGetter world, BlockPos pos, BlockState state, ItemStack stack, List<Enchantment> influencedEnchants);

	default float[] getModifiedColorComponents(BlockGetter world, BlockPos pos, BlockState state, float[] colorComponents) {
		return colorComponents;
	}
}
