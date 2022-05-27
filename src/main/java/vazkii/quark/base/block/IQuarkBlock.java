package vazkii.quark.base.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.common.extensions.IForgeBlock;
import vazkii.quark.base.datagen.QuarkBlockStateProvider;
import vazkii.quark.base.datagen.QuarkBlockTagsProvider;
import vazkii.quark.base.datagen.QuarkItemTagsProvider;
import vazkii.quark.base.datagen.QuarkLootTableProvider;
import vazkii.quark.base.module.QuarkModule;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.BooleanSupplier;

import static net.minecraftforge.client.model.generators.ModelProvider.BLOCK_FOLDER;

/**
 * @author WireSegal
 * Created at 1:14 PM on 9/19/19.
 */
public interface IQuarkBlock extends IForgeBlock {

	default Block getBlock() {
		return (Block) this;
	}

	@Nullable
	QuarkModule getModule();

	IQuarkBlock setCondition(BooleanSupplier condition);

	boolean doesConditionApply();

	default boolean isEnabled() {
		QuarkModule module = getModule();
		return module != null && module.enabled && doesConditionApply();
	}

	@Override
	default int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		if (state.getValues().containsKey(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED))
			return 0;

		Material material = state.getMaterial();
		if (material == Material.WOOL || material == Material.LEAVES)
			return 60;
		ResourceLocation loc = state.getBlock().getRegistryName();
		if (loc != null && (loc.getPath().endsWith("_log") || loc.getPath().endsWith("_wood")) && state.getMaterial().isFlammable())
			return 5;
		return state.getMaterial().isFlammable() ? 20 : 0;
	}

	@Override
	default int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		if (state.getValues().containsKey(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED))
			return 0;

		Material material = state.getMaterial();
		if (material == Material.WOOL || material == Material.LEAVES)
			return 30;
		return state.getMaterial().isFlammable() ? 5 : 0;
	}

	@Nullable
	default TagKey<Block> mineWith() {
		Material material = getBlock().defaultBlockState().getMaterial();
		if (material == Material.WOOD || material == Material.VEGETABLE || material == Material.BAMBOO || material == Material.NETHER_WOOD)
			return BlockTags.MINEABLE_WITH_AXE;
		else if (material == Material.LEAVES || material == Material.SCULK || material == Material.PLANT || material == Material.GRASS)
			return BlockTags.MINEABLE_WITH_HOE;
		else if (material == Material.STONE || material == Material.METAL || material == Material.AMETHYST ||
				material == Material.SHULKER_SHELL || material == Material.HEAVY_METAL || material == Material.ICE || material == Material.ICE_SOLID)
			return BlockTags.MINEABLE_WITH_PICKAXE;
		else if (material == Material.SAND || material == Material.CLAY || material == Material.DIRT)
			return BlockTags.MINEABLE_WITH_SHOVEL;
		return null;
	}

	default ResourceLocation blockTexture(QuarkBlockStateProvider states) {
		ResourceLocation name = getBlock().getRegistryName();
		return new ResourceLocation(name.getNamespace(), BLOCK_FOLDER + "/" + name.getPath());
	}

	default void dataGen(QuarkBlockStateProvider states) {
		states.cubeBlockAndItem(getBlock());
	}

	default void dataGen(QuarkLootTableProvider tableProvider, Map<Block, LootTable.Builder> lootTables) {
		if (getBlock().asItem() != Items.AIR)
			tableProvider.dropSelfTable(lootTables, getBlock());
	}

	default void dataGen(QuarkItemTagsProvider itemTags) {
		// NO-OP
	}

	default void dataGen(QuarkBlockTagsProvider blockTags) {
		// NO-OP
	}
}
