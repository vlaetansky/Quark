package vazkii.quark.content.building.block;

import net.minecraft.world.item.CreativeModeTab;
import vazkii.quark.base.block.QuarkGlassBlock;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.handler.RenderLayerHandler.RenderTypeSkeleton;
import vazkii.quark.base.module.QuarkModule;

public class FramedGlassBlock extends QuarkGlassBlock {

	public FramedGlassBlock(String regname, QuarkModule module, CreativeModeTab creativeTab, Properties properties, boolean translucent) {
		super(regname, module, creativeTab, properties);
		
		RenderLayerHandler.setRenderType(this, translucent ? RenderTypeSkeleton.TRANSLUCENT : RenderTypeSkeleton.CUTOUT);
	}
	
}
