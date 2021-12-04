package vazkii.quark.content.world.module.underground;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.BiomeDictionary;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.handler.VariantHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.content.world.config.UndergroundBiomeConfig;
import vazkii.quark.content.world.gen.underground.PermafrostUndergroundBiome;

// TODO NOW move to mountains
@LoadModule(category = ModuleCategory.WORLD)
public class PermafrostUndergroundBiomeModule extends UndergroundBiomeModule {

	public static QuarkBlock permafrost;
	
	@Override
	public void construct() {
		permafrost = new QuarkBlock("permafrost", this, CreativeModeTab.TAB_BUILDING_BLOCKS, 
				Block.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_BLUE)
				.requiresCorrectToolForDrops()
				.strength(1.5F, 10F)
				.sound(SoundType.STONE));
		
		VariantHandler.addSlabStairsWall(permafrost);
		VariantHandler.addSlabStairsWall(new QuarkBlock("permafrost_bricks", this, CreativeModeTab.TAB_BUILDING_BLOCKS, Block.Properties.copy(permafrost)));
		
		super.construct();
	}
	
	@Override
	protected UndergroundBiomeConfig getBiomeConfig() {
		return new UndergroundBiomeConfig(new PermafrostUndergroundBiome(), 80, BiomeDictionary.Type.SNOWY);
	}
	
	@Override
	protected String getBiomeName() {
		return "permafrost";
	}

}
