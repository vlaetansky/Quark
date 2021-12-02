package vazkii.quark.content.building.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import vazkii.quark.base.block.QuarkFlammableBlock;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.building.module.ThatchModule;

public class ThatchBlock extends QuarkFlammableBlock {

	public ThatchBlock(QuarkModule module) {
		super("thatch", module, CreativeModeTab.TAB_BUILDING_BLOCKS, 300,
				Block.Properties.of(Material.GRASS, MaterialColor.COLOR_YELLOW)
//				.harvestTool(ToolType.HOE) TODO TAG
				.strength(0.5F)
				.sound(SoundType.GRASS));
	}
	
	@Override
	public void fallOn(Level worldIn, BlockState state, BlockPos pos, Entity entityIn, float fallDistance) {
		entityIn.causeFallDamage(fallDistance, (float) ThatchModule.fallDamageMultiplier, DamageSource.FALL);
	}

}
