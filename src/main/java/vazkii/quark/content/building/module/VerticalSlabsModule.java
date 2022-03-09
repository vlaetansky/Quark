package vazkii.quark.content.building.module;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableSet;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ToolActions;
import vazkii.quark.base.handler.ToolInteractionHandler;
import vazkii.quark.base.handler.VariantHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.building.block.VerticalSlabBlock;
import vazkii.quark.content.building.block.WeatheringCopperVerticalSlabBlock;

@LoadModule(category = ModuleCategory.BUILDING)
public class VerticalSlabsModule extends QuarkModule {

	@Override
	public void postRegister() {
		ImmutableSet.of(Blocks.ACACIA_SLAB, Blocks.ANDESITE_SLAB, Blocks.BIRCH_SLAB, Blocks.BRICK_SLAB, Blocks.COBBLESTONE_SLAB, 
				Blocks.CUT_RED_SANDSTONE_SLAB, Blocks.CUT_SANDSTONE_SLAB, Blocks.DARK_OAK_SLAB, Blocks.DARK_PRISMARINE_SLAB, Blocks.DIORITE_SLAB, 
				Blocks.END_STONE_BRICK_SLAB, Blocks.GRANITE_SLAB, Blocks.JUNGLE_SLAB, Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.MOSSY_STONE_BRICK_SLAB, 
				Blocks.NETHER_BRICK_SLAB, Blocks.OAK_SLAB, Blocks.POLISHED_ANDESITE_SLAB, Blocks.POLISHED_DIORITE_SLAB, Blocks.POLISHED_GRANITE_SLAB, 
				Blocks.PRISMARINE_SLAB, Blocks.PRISMARINE_BRICK_SLAB, Blocks.PURPUR_SLAB, Blocks.QUARTZ_SLAB, Blocks.RED_NETHER_BRICK_SLAB, 
				Blocks.RED_SANDSTONE_SLAB, Blocks.SANDSTONE_SLAB, Blocks.SMOOTH_QUARTZ_SLAB, Blocks.SMOOTH_RED_SANDSTONE_SLAB, Blocks.SMOOTH_SANDSTONE_SLAB, 
				Blocks.SMOOTH_STONE_SLAB, Blocks.SPRUCE_SLAB, Blocks.STONE_SLAB, Blocks.STONE_BRICK_SLAB, Blocks.BLACKSTONE_SLAB, Blocks.POLISHED_BLACKSTONE_SLAB,
				Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, Blocks.CRIMSON_SLAB, Blocks.WARPED_SLAB, Blocks.COBBLED_DEEPSLATE_SLAB, Blocks.POLISHED_DEEPSLATE_SLAB,
				Blocks.DEEPSLATE_BRICK_SLAB, Blocks.DEEPSLATE_TILE_SLAB)
		.forEach(b -> new VerticalSlabBlock(b, this));

		List<WeatheringCopperVerticalSlabBlock> copperVerticalSlabs = new ArrayList<>();
		ImmutableSet.of(
				Pair.of(Blocks.CUT_COPPER_SLAB, Blocks.WAXED_CUT_COPPER_SLAB), 
				Pair.of(Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB),
				Pair.of(Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB),
				Pair.of(Blocks.OXIDIZED_CUT_COPPER_SLAB, Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB))
		.forEach(p -> {
			WeatheringCopperVerticalSlabBlock cleanSlab = new WeatheringCopperVerticalSlabBlock(p.getLeft(), this);
			VerticalSlabBlock waxedSlab = new VerticalSlabBlock(p.getRight(), this);
			
			copperVerticalSlabs.add(cleanSlab);
			ToolInteractionHandler.registerWaxedBlock(cleanSlab, waxedSlab);
		});
		
		for(int i = 1; i < copperVerticalSlabs.size(); i++) {
			WeatheringCopperVerticalSlabBlock prev = copperVerticalSlabs.get(i - 1);
			WeatheringCopperVerticalSlabBlock next = copperVerticalSlabs.get(i);
			ToolInteractionHandler.registerInteraction(ToolActions.AXE_SCRAPE, next, prev);
			
			prev.next = next;
		}

		VariantHandler.SLABS.forEach(b ->  {
			if(b instanceof IVerticalSlabProvider) 
				((IVerticalSlabProvider) b).getVerticalSlab(b, this);
			else new VerticalSlabBlock(b, this);
		});
	}

	public static interface IVerticalSlabProvider {
		VerticalSlabBlock getVerticalSlab(Block block, QuarkModule module);

	}

}
