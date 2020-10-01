package vazkii.quark.building.module;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemGroup;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.handler.FuelHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.Module;
import vazkii.quark.base.module.ModuleCategory;

@LoadModule(category = ModuleCategory.BUILDING)
public class QuiltedWoolModule extends Module {

	@Override
	public void construct() {
		for(DyeColor dye : DyeColor.values()) {
			Block b = new QuarkBlock(dye.getTranslationKey() + "_quilted_wool", this, ItemGroup.BUILDING_BLOCKS,
					Block.Properties.create(Material.WOOL, dye.getMapColor())
					.hardnessAndResistance(0.8F)
					.sound(SoundType.CLOTH));
			
			FuelHandler.addFuel(b, 100);
		}
	}
	
}
