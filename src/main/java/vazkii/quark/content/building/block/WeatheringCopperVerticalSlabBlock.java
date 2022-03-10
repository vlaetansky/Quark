package vazkii.quark.content.building.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.quark.base.module.QuarkModule;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Random;

public class WeatheringCopperVerticalSlabBlock extends VerticalSlabBlock implements WeatheringCopper {
	private final WeatheringCopper.WeatherState weatherState;
	public WeatheringCopperVerticalSlabBlock next;

	public WeatheringCopperVerticalSlabBlock(Block parent, QuarkModule module) {
		super(parent, module);
		weatherState = ((WeatheringCopper) parent).getAge();
	}

	@Override
	public void randomTick(@Nonnull BlockState state, @Nonnull ServerLevel world, @Nonnull BlockPos pos, @Nonnull Random random) {
		this.onRandomTick(state, world, pos, random);
	}

	@Override
	public boolean isRandomlyTicking(@Nonnull BlockState state) {
		return getNext(state).isPresent();
	}

	@Nonnull
	@Override
	public Optional<BlockState> getNext(@Nonnull BlockState state) {
		return next == null ? Optional.empty() : Optional.of(next.withPropertiesOf(state));
	}

	@Nonnull
	@Override
	public WeatheringCopper.WeatherState getAge() {
		return weatherState;
	}

}
