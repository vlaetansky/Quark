package vazkii.quark.content.client.module;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.IRegistryDelegate;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.type.inputtable.ConvulsionMatrixConfig;

@LoadModule(category = ModuleCategory.CLIENT)
public class GreenerGrassModule extends QuarkModule {

	@Config public static boolean affectLeaves = true;

	@Config public static List<String> blockList = Lists.newArrayList(
			"minecraft:large_fern", 
			"minecraft:tall_grass",
			"minecraft:grass_block",
			"minecraft:fern",
			"minecraft:grass",
			"minecraft:potted_fern",
			"minecraft:sugar_cane",
			"environmental:giant_tall_grass",
			"valhelsia_structures:grass_block");

	@Config public static List<String> leavesList = Lists.newArrayList(
			"minecraft:spruce_leaves", 
			"minecraft:birch_leaves",
			"minecraft:oak_leaves",
			"minecraft:jungle_leaves",
			"minecraft:acacia_leaves",
			"minecraft:dark_oak_leaves",
			"atmospheric:rosewood_leaves",
			"atmospheric:morado_leaves",
			"atmospheric:yucca_leaves",
			"autumnity:maple_leaves",
			"environmental:willow_leaves",
			"environmental:hanging_willow_leaves",
			"minecraft:vine");
	
	@Config public static ConvulsionMatrixConfig colorMatrix = new ConvulsionMatrixConfig(new double[] {
			0.89, 0.00, 0.00,
			0.00, 1.11, 0.00,
			0.00, 0.00, 0.89
	});

	@Override
	public void firstClientTick() {
		registerGreenerColor(blockList, false);
		registerGreenerColor(leavesList, true);
	}

	@OnlyIn(Dist.CLIENT)
	private void registerGreenerColor(Iterable<String> ids, boolean leaves) {
		BlockColors colors = Minecraft.getInstance().getBlockColors();

		// Can't be AT'd as it's changed by forge
		Map<IRegistryDelegate<Block>, BlockColor> map = ObfuscationReflectionHelper.getPrivateValue(BlockColors.class, colors, "f_92571_");

		for(String id : ids) {
			Registry.BLOCK.getOptional(new ResourceLocation(id)).ifPresent(b -> {
				if (b.delegate == null)
					return;
				BlockColor color = map.get(b.delegate);
				if(color != null)
					colors.register(getGreenerColor(color, leaves), b);
			});
		}
	}

	@OnlyIn(Dist.CLIENT)
	private BlockColor getGreenerColor(BlockColor color, boolean leaves) {
		return (state, world, pos, tintIndex) -> {
			int originalColor = color.getColor(state, world, pos, tintIndex);
			if(!enabled || (leaves && !affectLeaves))
				return originalColor;
			
			return colorMatrix.convolve(originalColor);
		};
	}

}
