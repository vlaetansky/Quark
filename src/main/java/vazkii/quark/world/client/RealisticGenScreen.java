package vazkii.quark.world.client;

import net.minecraft.client.gui.screen.BiomeGeneratorTypeScreens;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import vazkii.quark.world.gen.RealisticChunkGenerator;

public class RealisticGenScreen extends BiomeGeneratorTypeScreens {

	public RealisticGenScreen() {
		super("quark.realistic");
		field_239068_c_.add(this);
	}

	@Override
	protected ChunkGenerator func_241869_a(Registry<Biome> biomeRegistry, Registry<DimensionSettings> settings, long seed) {
		return new RealisticChunkGenerator(new OverworldBiomeProvider(seed, false, false, biomeRegistry), seed, DimensionSettings.Preset.field_236122_b_.func_236137_b_());
	}

}
