package vazkii.quark.content.building.block;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.quark.base.module.QuarkModule;

public class WeatheringCopperVerticalSlabBlock extends VerticalSlabBlock implements WeatheringCopper {
	private final WeatheringCopper.WeatherState weatherState;

	public WeatheringCopperVerticalSlabBlock(Block parent, QuarkModule module) {
		super(parent, module);
		weatherState = ((WeatheringCopper) parent).getAge();
	}

	@Override
	public void randomTick(BlockState p_154942_, ServerLevel p_154943_, BlockPos p_154944_, Random p_154945_) {
		this.onRandomTick(p_154942_, p_154943_, p_154944_, p_154945_);
	}

	@Override
	public boolean isRandomlyTicking(BlockState p_154947_) {
		return WeatheringCopper.getNext(p_154947_.getBlock()).isPresent();
	}

	@Override
	public WeatheringCopper.WeatherState getAge() {
		return weatherState;
	}

}
