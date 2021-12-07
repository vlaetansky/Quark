package vazkii.quark.content.world.block;

import java.util.Random;

import javax.annotation.Nullable;

import com.mojang.math.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import vazkii.quark.base.block.QuarkGlassBlock;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.handler.RenderLayerHandler.RenderTypeSkeleton;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.world.module.SpiralSpiresModule;
import vazkii.quark.content.world.module.underground.CaveCrystalUndergroundBiomeModule;

/**
 * @author WireSegal
 * Created at 12:31 PM on 9/19/19.
 */
public class CaveCrystalBlock extends QuarkGlassBlock {

	public final float[] colorComponents;
	final boolean waxed;

	public CaveCrystalClusterBlock cluster;
	public CaveCrystalBlock alternate;

	public CaveCrystalBlock(String regname, int color, QuarkModule module, MaterialColor materialColor, boolean waxed) {
		super(regname, module, CreativeModeTab.TAB_DECORATIONS,
				Block.Properties.of(Material.GLASS, materialColor)
				.strength(0.3F, 0F)
				.sound(SoundType.AMETHYST)
				.lightLevel(b -> 11)
				.requiresCorrectToolForDrops()
				.randomTicks()
				.noOcclusion());

		float r = ((color >> 16) & 0xff) / 255f;
		float g = ((color >> 8) & 0xff) / 255f;
		float b = (color & 0xff) / 255f;
		colorComponents = new float[]{r, g, b};
		this.waxed = waxed;

		RenderLayerHandler.setRenderType(this, RenderTypeSkeleton.TRANSLUCENT);
	}

	private boolean canGrow(Level world, BlockPos pos) {
		if(!waxed && CaveCrystalUndergroundBiomeModule.caveCrystalGrowthChance >= 1 && pos.getY() < 24 && world.isEmptyBlock(pos.above())) {
			int i;
			for(i = 1; world.getBlockState(pos.below(i)).getBlock() == this; ++i);

			return i < 4;
		}
		return false;
	}

	@Override
	public BlockState getToolModifiedState(BlockState state, Level world, BlockPos pos, Player player, ItemStack stack, ToolAction toolAction) {
		if(waxed && toolAction.equals(ToolActions.AXE_WAX_OFF))
			return alternate.defaultBlockState();
		
		return super.getToolModifiedState(state, world, pos, player, stack, toolAction);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
		ItemStack stack = player.getItemInHand(hand);
		if(!waxed) {
			if(stack.getItem() == Items.HONEYCOMB) {
				if(!world.isClientSide) {
					world.setBlockAndUpdate(pos, alternate.defaultBlockState());
					world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.HONEY_BLOCK_PLACE, SoundSource.PLAYERS, 1F, 1F);
				}
				
				if(!player.isCreative())
					stack.setCount(stack.getCount() - 1);
				
				return InteractionResult.SUCCESS;
			}
		}
		
		return InteractionResult.PASS;
	}

	@Override
	public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random random) {
		if(canGrow(worldIn, pos) && random.nextInt(CaveCrystalUndergroundBiomeModule.caveCrystalGrowthChance) == 0) {
			BlockState down = worldIn.getBlockState(pos.below());
			BlockPos up = pos.above();
			worldIn.setBlockAndUpdate(up, state);

			if(down.getBlock() == SpiralSpiresModule.myalite_crystal && ModuleLoader.INSTANCE.isModuleEnabled(SpiralSpiresModule.class) && SpiralSpiresModule.renewableMyalite)
				worldIn.setBlockAndUpdate(pos, SpiralSpiresModule.myalite_crystal.defaultBlockState());
			else for(Direction d : Direction.values()) {
				BlockPos offPos = up.relative(d);
				if(worldIn.isEmptyBlock(offPos) && random.nextInt(3) == 0)
					worldIn.setBlockAndUpdate(offPos, cluster.defaultBlockState().setValue(CaveCrystalClusterBlock.FACING, d));
			}
		}
	}

	@Override
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {
		if(canGrow(worldIn, pos)) {
			double x = (double)pos.getX() + rand.nextDouble();
			double y = (double)pos.getY() + rand.nextDouble();
			double z = (double)pos.getZ() + rand.nextDouble();

			worldIn.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, x, y, z, colorComponents[0], colorComponents[1], colorComponents[2]);
		}

		if(!waxed)
			for(int i = 0; i < 4; i++) {
				double range = 5;

				double ox = rand.nextDouble() * range - (range / 2);
				double oy = rand.nextDouble() * range - (range / 2);
				double oz = rand.nextDouble() * range - (range / 2);

				double x = (double)pos.getX() + 0.5 + ox;
				double y = (double)pos.getY() + 0.5 + oy;
				double z = (double)pos.getZ() + 0.5 + oz;

				float size = 0.4F + rand.nextFloat() * 0.5F;

				if(rand.nextDouble() < 0.1) {
					double ol = ((ox * ox) + (oy * oy) + (oz * oz)) * -2;
					if(ol == 0)
						ol = 0.0001;
					worldIn.addParticle(ParticleTypes.END_ROD, x, y, z, ox / ol, oy / ol, oz / ol);
				}

				worldIn.addParticle(new DustParticleOptions(new Vector3f(colorComponents[0], colorComponents[1], colorComponents[2]), size), x, y, z, 0, 0, 0);
			}
	}

	@Nullable
	@Override
	public float[] getBeaconColorMultiplier(BlockState state, LevelReader world, BlockPos pos, BlockPos beaconPos) {
		return colorComponents;
	}

}
