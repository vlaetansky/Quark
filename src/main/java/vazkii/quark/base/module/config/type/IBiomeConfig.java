package vazkii.quark.base.module.config.type;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import java.util.Optional;

public interface IBiomeConfig extends IConfigType {

	default boolean canSpawn(Biome b) {
		return canSpawn(b.getRegistryName());
	}

	default boolean canSpawn(BiomeLoadingEvent b) {
		return canSpawn(b.getName());
	}

	default boolean canSpawn(Holder<Biome> b) {
		Either<ResourceKey<Biome>, Biome> either = b.unwrap();

		Optional<ResourceKey<Biome>> optRk = either.left();
		if(optRk.isPresent())
			return canSpawn(optRk.get().location());

		Optional<Biome> optBm = either.right();
		return optBm.filter(this::canSpawn).isPresent();

	}

	boolean canSpawn(ResourceLocation b);

}
