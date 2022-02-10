package vazkii.quark.content.world.block;

import java.util.Random;
import java.util.function.BooleanSupplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.module.QuarkModule;

public class HugeGlowShroomBlock extends HugeMushroomBlock implements IQuarkBlock {

	private final QuarkModule module;
	private final boolean glowing;

	public HugeGlowShroomBlock(String name, QuarkModule module, final boolean glowing) {
		super(Block.Properties.copy(Blocks.RED_MUSHROOM_BLOCK)
				.lightLevel(b -> glowing ? 12 : 0)
				.randomTicks()
				.noOcclusion());

		this.module = module;
		this.glowing = glowing;
		
		RegistryHelper.registerBlock(this, name);
		RegistryHelper.setCreativeTab(this, CreativeModeTab.TAB_DECORATIONS);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {
		super.animateTick(stateIn, worldIn, pos, rand);

		if(glowing && rand.nextInt(10) == 0 && worldIn.getBlockState(pos.below()).isAir())
			worldIn.addParticle(ParticleTypes.END_ROD, pos.getX() + rand.nextDouble(), pos.getY(), pos.getZ() + rand.nextDouble(), 0, -0.1, 0);
	}

	public static boolean place(Level worldIn, Random rand, BlockPos pos) {
		if (worldIn.isInWorldBounds(pos)) {
			Block block = worldIn.getBlockState(pos.below()).getBlock();
			if (block != Blocks.DEEPSLATE) {
				return false;
			} else {
				BlockPos.MutableBlockPos placePos = new BlockPos.MutableBlockPos();
				worldIn.setBlock(placePos, Blocks.GLOWSTONE.defaultBlockState(), 0);


				return true;
			}
		} else {
			return false;
		}
	}

	public boolean isEnabled() {
		return module != null && module.enabled;
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
		return true;
	}

}