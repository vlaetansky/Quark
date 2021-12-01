package vazkii.quark.content.world.block;

import java.util.Random;
import java.util.function.BooleanSupplier;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolType;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.handler.RenderLayerHandler.RenderTypeSkeleton;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.world.module.BlossomTreesModule;

public class BlossomLeavesBlock extends LeavesBlock implements IQuarkBlock {

	private final QuarkModule module;
	private BooleanSupplier enabledSupplier = () -> true;
	
	public BlossomLeavesBlock(String colorName, QuarkModule module, MaterialColor color) {
		super(Block.Properties.of(Material.LEAVES, color)
				.strength(0.2F)
				.randomTicks()
				.sound(SoundType.GRASS)
				.harvestTool(ToolType.HOE)
				.noOcclusion()
				.isValidSpawn((s, r, p, t) -> false)
				.isSuffocating((s, r, p) -> false)
				.isViewBlocking((s, r, p) -> false));
		
		this.module = module;

		RegistryHelper.registerBlock(this, colorName + "_blossom_leaves");
		RegistryHelper.setCreativeTab(this, CreativeModeTab.TAB_DECORATIONS);
		
		RenderLayerHandler.setRenderType(this, RenderTypeSkeleton.CUTOUT_MIPPED);
	}
	
	@Override
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {
		if(worldIn.isEmptyBlock(pos.below()) && rand.nextInt(5) == 0 && BlossomTreesModule.dropLeafParticles) {
			double windStrength = 5 + Math.cos((double) worldIn.getGameTime() / 2000) * 2;
			double windX = Math.cos((double) worldIn.getGameTime() / 1200) * windStrength;
			double windZ = Math.sin((double) worldIn.getGameTime() / 1000) * windStrength;
			
			worldIn.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, stateIn), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, windX, -1.0, windZ);
		}
	}

	@Nullable
	@Override
	public QuarkModule getModule() {
		return module;
	}

	@Override
	public BlossomLeavesBlock setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

}
