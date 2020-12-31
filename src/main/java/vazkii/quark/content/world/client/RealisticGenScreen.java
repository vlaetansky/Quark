package vazkii.quark.content.world.client;

import net.minecraft.client.gui.screen.BiomeGeneratorTypeScreens;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import vazkii.quark.content.world.gen.RealisticChunkGenerator;

public class RealisticGenScreen extends BiomeGeneratorTypeScreens {

	final boolean large;
	
	public RealisticGenScreen(String name, boolean large) {
		super("quark." + name);
		field_239068_c_.add(this);
		
		this.large = large;
	}

	@Override
	protected ChunkGenerator func_241869_a(Registry<Biome> biomeRegistry, Registry<DimensionSettings> settings, long seed) {
		return new RealisticChunkGenerator(new OverworldBiomeProvider(seed, false, large, biomeRegistry), seed,
				() -> settings.getOrThrow(DimensionSettings.field_242734_c));
	}

}
