package vazkii.quark.integration.terrablender;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.mojang.datafixers.util.Pair;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.Climate.ParameterPoint;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import terrablender.api.BiomeProvider;
import terrablender.api.BiomeProviders;
import terrablender.worldgen.BiomeProviderUtils;
import terrablender.worldgen.TBClimate;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.UndergroundBiomeHandler;
import vazkii.quark.base.handler.UndergroundBiomeHandler.Proxy;
import vazkii.quark.base.handler.UndergroundBiomeHandler.UndergroundBiomeSkeleton;

public class TerraBlenderIntegration implements Supplier<UndergroundBiomeHandler.Proxy> {

	private TBProxy proxy;

	@Override
	public Proxy get() {
		if(proxy == null)
			proxy = new TBProxy();

		return proxy;
	}

	class QuarkBiomeProvider extends BiomeProvider {

		public QuarkBiomeProvider() {
			super(new ResourceLocation(Quark.MOD_ID, "biome_provider"), 1);
		}

		@Override
		public void addOverworldBiomes(Registry<Biome> registry, Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper) {
			for(UndergroundBiomeSkeleton skeleton : proxy.skeletons)
				if(skeleton.module().enabled) {
					ResourceKey<Biome> resourceKey = ResourceKey.create(Registry.BIOME_REGISTRY, skeleton.biome());
					mapper.accept(Pair.of(convertParams(skeleton.climate()), resourceKey));
				}
		}

		private TBClimate.ParameterPoint convertParams(Climate.ParameterPoint vanilla) {
			return TBClimate.parameters(vanilla.temperature(), vanilla.humidity(), vanilla.continentalness(), vanilla.erosion(), vanilla.weirdness(), vanilla.depth(), BiomeProviderUtils.getUniquenessParameter(getIndex()), vanilla.offset());
		}

	}

	class TBProxy extends UndergroundBiomeHandler.Proxy {

		@Override
		public void init(FMLCommonSetupEvent event) {
			event.enqueueWork(() -> {
				BiomeProviders.register(new QuarkBiomeProvider());
			});
		}

		@Override
		public void addUndergroundBiomes(OverworldBiomeBuilder builder, Consumer<Pair<ParameterPoint, ResourceKey<Biome>>> consumer) {
			// Nothing happens here as we're using TB's methods instead
		}

	}

}
