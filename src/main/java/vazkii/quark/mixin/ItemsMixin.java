package vazkii.quark.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.world.item.Items;

@Mixin(Items.class)
public class ItemsMixin {

	@Overwrite
	private static <T> Optional<T> ifPart2(T val) {
		return Optional.of(val);
	}

}
