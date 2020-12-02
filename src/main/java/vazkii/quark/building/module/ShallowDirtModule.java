package vazkii.quark.building.module;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.building.block.ShallowDirtBlock;
import vazkii.quark.tweaks.module.DirtToPathModule;

@LoadModule(category = ModuleCategory.BUILDING, hasSubscriptions = true)
public class ShallowDirtModule extends QuarkModule {
	
	public static Block shallow_dirt;
	
	@Override
	public void construct() {
		shallow_dirt = new ShallowDirtBlock(this);
	}
	
	@SubscribeEvent
	public void onRightClick(PlayerInteractEvent.RightClickBlock event) {
		DirtToPathModule.doTheShovelThingHomie(event, ToolType.HOE, Blocks.GRASS_PATH, shallow_dirt);
	}

}
