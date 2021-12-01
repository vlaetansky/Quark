package vazkii.quark.content.automation.module;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemGroup;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.automation.block.RedstoneRandomizerBlock;

/**
 * @author WireSegal
 * Created at 10:34 AM on 8/26/19.
 */
@LoadModule(category = ModuleCategory.AUTOMATION)
public class RedstoneRandomizerModule extends QuarkModule {

    @Override
    public void construct() {
        new RedstoneRandomizerBlock("redstone_randomizer", this, ItemGroup.REDSTONE, Block.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(0).sound(SoundType.WOOD));
    }
}
