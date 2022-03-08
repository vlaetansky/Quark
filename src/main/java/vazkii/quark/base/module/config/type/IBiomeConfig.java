package vazkii.quark.base.module.config.type;

import java.util.Optional;

import com.mojang.datafixers.util.Either;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.event.world.BiomeLoadingEvent;

public interface IBiomeConfig extends IConfigType {

	public default boolean canSpawn(Biome b) {
		return canSpawn(b.getRegistryName());
	}
	
	public default boolean canSpawn(BiomeLoadingEvent b) {
		return canSpawn(b.getName());
	}
	
	public default boolean canSpawn(Holder<Biome> b) {
		Either<ResourceKey<Biome>, Biome> either = b.unwrap();
		
		Optional<ResourceKey<Biome>> optRk = either.left();
		if(optRk.isPresent())
			return canSpawn(optRk.get().getRegistryName());
		
		Optional<Biome> optBm = either.right();
		if(optBm.isPresent())
			return canSpawn(optBm.get());
		
		return false;
	}
	
	public boolean canSpawn(ResourceLocation b);
	
}
