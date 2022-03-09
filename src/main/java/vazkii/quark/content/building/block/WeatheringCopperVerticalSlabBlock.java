package vazkii.quark.content.building.block;

import java.util.Optional;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.quark.base.module.QuarkModule;

import javax.annotation.Nonnull;

public class WeatheringCopperVerticalSlabBlock extends VerticalSlabBlock implements WeatheringCopper {
	private final WeatheringCopper.WeatherState weatherState;
	public WeatheringCopperVerticalSlabBlock next;

	public WeatheringCopperVerticalSlabBlock(Block parent, QuarkModule module) {
		super(parent, module);
		weatherState = ((WeatheringCopper) parent).getAge();
	}

	@Override
	public void randomTick(@Nonnull BlockState p_154942_, @Nonnull ServerLevel p_154943_, @Nonnull BlockPos p_154944_, @Nonnull Random p_154945_) {
		this.onRandomTick(p_154942_, p_154943_, p_154944_, p_154945_);
	}

	@Override
	public boolean isRandomlyTicking(@Nonnull BlockState p_154947_) {
		return getNext(p_154947_).isPresent();
	}

	@Nonnull
	@Override
	public Optional<BlockState> getNext(@Nonnull BlockState p_154893_) {
		return next == null ? Optional.empty() : Optional.of(next.withPropertiesOf(p_154893_));
	}

	@Nonnull
	@Override
	public WeatheringCopper.WeatherState getAge() {
		return weatherState;
	}

}
