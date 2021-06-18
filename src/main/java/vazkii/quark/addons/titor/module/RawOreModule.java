package vazkii.quark.addons.titor.module;

import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.item.QuarkItem;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = ModuleCategory.TITOR)
public class RawOreModule extends QuarkModule {

	public static Item raw_gold, raw_iron;
	public static Block raw_gold_block, raw_iron_block;
	
	@Config(flag = "raw_block_smelting") 
	public static boolean allowFullBlockSmelting = true;
	
	@Override
	public void construct() {
		raw_gold = new QuarkItem("raw_gold", this, new Item.Properties().group(ItemGroup.MATERIALS));
		raw_iron = new QuarkItem("raw_iron", this, new Item.Properties().group(ItemGroup.MATERIALS));
		
		raw_gold_block = new QuarkBlock("raw_gold_block", this, ItemGroup.BUILDING_BLOCKS, Properties.from(Blocks.GOLD_BLOCK));
		raw_iron_block = new QuarkBlock("raw_iron_block", this, ItemGroup.BUILDING_BLOCKS, Properties.from(Blocks.IRON_BLOCK));
	}
	
}
