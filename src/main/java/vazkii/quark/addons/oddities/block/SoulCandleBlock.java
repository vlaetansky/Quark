package vazkii.quark.addons.oddities.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import vazkii.quark.base.block.QuarkCandleBlock;
import vazkii.quark.base.module.QuarkModule;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.function.ToIntFunction;

public class SoulCandleBlock extends QuarkCandleBlock {

	public static final ToIntFunction<BlockState> LIGHT_EMISSION = (state) -> state.getValue(LIT) ? 2 * state.getValue(CANDLES) : 0;

	public SoulCandleBlock(String regname, QuarkModule module, CreativeModeTab creativeTab, Properties properties) {
		super(regname, module, creativeTab, properties);
	}

	@Override
	public void animateTick(BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull Random rand) {
		if (state.getValue(LIT)) {
			this.getParticleOffsets(state).forEach((offset) ->
					addSoulfireParticlesAndSound(world, offset.add(pos.getX(), pos.getY(), pos.getZ()), rand));
		}
	}


	private static void addSoulfireParticlesAndSound(Level world, Vec3 pos, Random rand) {
		float f = rand.nextFloat();
		if (f < 0.3F) {
			world.addParticle(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, 0.0D, 0.0D, 0.0D);
			if (f < 0.17F) {
				world.playLocalSound(pos.x + 0.5D, pos.y + 0.5D, pos.z + 0.5D, SoundEvents.CANDLE_AMBIENT, SoundSource.BLOCKS, 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
			}
		}

		world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, pos.x, pos.y, pos.z, 0.0D, 0.0D, 0.0D);
	}
}
