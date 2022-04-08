package vazkii.quark.mixin;

import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import vazkii.quark.base.block.CustomWeatheringCopper;

import java.util.Optional;

@Mixin(WeatheringCopper.class)
public interface WeatheringCopperMixin {

	// Why. Why can I not inject into static interface methods. Why.

	/**
	 * @author WireSegal
	 * @reason The list of copper states is missing a way to add other blocks.
	 */
	@Overwrite
	static Optional<BlockState> getPrevious(BlockState state) {
		if (state.getBlock() instanceof CustomWeatheringCopper copper)
			return copper.getPrevious(state);
		return WeatheringCopper.getPrevious(state.getBlock()).map((block) -> block.withPropertiesOf(state));
	}

	/**
	 * @author WireSegal
	 * @reason The list of copper states is missing a way to add other blocks.
	 */
	@Overwrite
	static BlockState getFirst(BlockState state) {
		if (state.getBlock() instanceof CustomWeatheringCopper copper)
			return copper.getFirst(state);
		return WeatheringCopper.getFirst(state.getBlock()).withPropertiesOf(state);
	}

}
