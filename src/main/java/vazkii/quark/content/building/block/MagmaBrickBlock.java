package vazkii.quark.content.building.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.SpawnPlacements.Type;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.handler.VariantHandler.IVariantsShouldBeEmissive;
import vazkii.quark.base.module.QuarkModule;

public class MagmaBrickBlock extends QuarkBlock implements IVariantsShouldBeEmissive {

	public MagmaBrickBlock(QuarkModule module) {
		super("magma_bricks", module, CreativeModeTab.TAB_BUILDING_BLOCKS, 
				Block.Properties.copy(Blocks.MAGMA_BLOCK)
				.strength(1.5F, 10F)
				.emissiveRendering((s, r, p) -> true));
	}
	
	@Override
	public boolean isFireSource(BlockState state, LevelReader world, BlockPos pos, Direction side) {
		return true;
	}
	
	@Override
	public boolean canCreatureSpawn(BlockState state, BlockGetter world, BlockPos pos, Type type, EntityType<?> entityType) {
		return entityType.fireImmune();
	}

}
 