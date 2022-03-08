package vazkii.quark.content.world.module;

import com.mojang.datafixers.util.Either;

import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.material.MaterialColor;
import vazkii.quark.base.handler.WoodSetHandler;
import vazkii.quark.base.handler.WoodSetHandler.WoodSet;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;

@LoadModule(category = ModuleCategory.WORLD)
public class AzaleaWoodModule extends QuarkModule {

	public static WoodSet woodSet;

	@Override
	public void construct() {
		woodSet = WoodSetHandler.addWoodSet(this, "azalea", MaterialColor.COLOR_LIGHT_GREEN, MaterialColor.COLOR_BROWN);
	}

	@Override
	public void enabledStatusChanged(boolean firstLoad, boolean oldStatus, boolean newStatus) { // TODO does this work
		Either<ResourceKey<ConfiguredFeature<TreeConfiguration, ?>>, ConfiguredFeature<TreeConfiguration, ?>> either = TreeFeatures.AZALEA_TREE.unwrap();
		if(either.right().isPresent()) {
			ConfiguredFeature<TreeConfiguration, ?> configured = either.right().get();
			TreeConfiguration config = configured.config();

			if(newStatus)
				config.trunkProvider = BlockStateProvider.simple(woodSet.log);
			else if(!firstLoad)
				config.trunkProvider = BlockStateProvider.simple(Blocks.OAK_LOG);
		}
	}

}
