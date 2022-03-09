package vazkii.quark.content.building.module;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import vazkii.quark.base.block.QuarkPaneBlock;
import vazkii.quark.base.handler.RenderLayerHandler.RenderTypeSkeleton;
import vazkii.quark.base.handler.StructureBlockReplacementHandler;
import vazkii.quark.base.handler.StructureBlockReplacementHandler.StructureHolder;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = ModuleCategory.BUILDING)
public class GoldBarsModule extends QuarkModule {

	@Config public static boolean generateInNetherFortress = true;
	
	public static boolean staticEnabled;
	
	public static Block gold_bars;
	
	@Override
	public void register() {
		gold_bars = new QuarkPaneBlock("gold_bars", this, Properties.copy(Blocks.IRON_BARS), RenderTypeSkeleton.CUTOUT);
		
		StructureBlockReplacementHandler.functions.add(GoldBarsModule::getGenerationBarBlockState);
	}
	
	@Override
	public void configChanged() {
		staticEnabled = enabled;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static BlockState getGenerationBarBlockState(BlockState current, StructureHolder structure) {
		if(staticEnabled && generateInNetherFortress && current.getBlock() == Blocks.NETHER_BRICK_FENCE) {
			ResourceLocation res = structure.currentStructure.getRegistryName();
			if(res == null)
				return null; // no change
			String name = res.toString();
			
			if("minecraft:fortress".equals(name)) {
				BlockState newState = gold_bars.defaultBlockState();
				for(Property prop : current.getProperties())
					// both blocks have same properties in vanilla, so this check isn't needed,
					// but some mods add additional block states to fences, then this would fail
					if (newState.hasProperty(prop))
					newState = newState.setValue(prop, current.getValue(prop));
				return newState;
			}
		}
		
		return null; // no change
	}

	
}
