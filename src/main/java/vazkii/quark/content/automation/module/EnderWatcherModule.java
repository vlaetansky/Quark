package vazkii.quark.content.automation.module;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.automation.block.EnderWatcherBlock;
import vazkii.quark.content.automation.tile.EnderWatcherTileEntity;

@LoadModule(category = ModuleCategory.AUTOMATION)
public class EnderWatcherModule extends QuarkModule {

	public static BlockEntityType<EnderWatcherTileEntity> enderWatcherTEType;

	@Override
	public void construct() {
		Block ender_watcher = new EnderWatcherBlock(this);
		enderWatcherTEType = BlockEntityType.Builder.of(EnderWatcherTileEntity::new, ender_watcher).build(null);
		RegistryHelper.register(enderWatcherTEType, "ender_watcher");
	}
	
}
