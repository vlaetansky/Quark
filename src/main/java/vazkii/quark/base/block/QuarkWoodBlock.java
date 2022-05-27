package vazkii.quark.base.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import vazkii.quark.base.datagen.QuarkBlockStateProvider;
import vazkii.quark.base.module.QuarkModule;

public class QuarkWoodBlock extends QuarkPillarBlock {

	private final Block parent;

	public QuarkWoodBlock(String regname, Block parent, QuarkModule module, CreativeModeTab creativeTab, Properties properties) {
		super(regname, module, creativeTab, properties);
		this.parent = parent;
	}

	@Override
	public void dataGen(QuarkBlockStateProvider states) {
		ModelFile cube = states.models().cubeAll(getRegistryName().getPath(), states.blockTexture(parent));
		states.getVariantBuilder(this).forAllStates((state) -> {
			Direction.Axis axis = state.getValue(AXIS);
			int x = 0, y = 0;

			if (!axis.isVertical())
				x = 90;
			if (axis == Direction.Axis.X)
				y = 90;
			return new ConfiguredModel[] { new ConfiguredModel(cube, x, y, false) };
		});
		states.simpleBlockItem(this, cube);
	}
}
