package vazkii.quark.content.world.module;

import net.minecraft.data.worldgen.features.TreeFeatures;
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
	public void register() {
		woodSet = WoodSetHandler.addWoodSet(this, "azalea", MaterialColor.COLOR_LIGHT_GREEN, MaterialColor.COLOR_BROWN);
	}

	@Override
	public void enabledStatusChanged(boolean firstLoad, boolean oldStatus, boolean newStatus) { // TODO does this work
		ConfiguredFeature<TreeConfiguration, ?> configured = null;
		try {
			configured = TreeFeatures.AZALEA_TREE.value();
		} catch(IllegalStateException e) {
			e.printStackTrace();
		}
		
		if(configured != null) {
			TreeConfiguration config = configured.config();
	
			if(newStatus)
				config.trunkProvider = BlockStateProvider.simple(woodSet.log);
			else if(!firstLoad)
				config.trunkProvider = BlockStateProvider.simple(Blocks.OAK_LOG);
		}
	}

}
