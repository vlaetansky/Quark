package vazkii.quark.content.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.client.model.generators.ModelFile;
import vazkii.quark.base.block.QuarkGlassBlock;
import vazkii.quark.base.datagen.QuarkBlockStateProvider;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.handler.RenderLayerHandler.RenderTypeSkeleton;
import vazkii.quark.base.module.QuarkModule;

import javax.annotation.Nullable;

public class MyaliteCrystalBlock extends QuarkGlassBlock implements IMyaliteColorProvider {

	public MyaliteCrystalBlock(QuarkModule module) {
		super("myalite_crystal", module, CreativeModeTab.TAB_DECORATIONS,
				Block.Properties.of(Material.GLASS, MaterialColor.COLOR_PURPLE)
				.strength(0.5F, 1200F)
				.sound(SoundType.GLASS)
				.lightLevel(b -> 14)
				.requiresCorrectToolForDrops()
				.randomTicks()
				.noOcclusion());

		RenderLayerHandler.setRenderType(this, RenderTypeSkeleton.TRANSLUCENT);
	}

	private static float[] decompColor(int color) {
		int r = (color & 0xFF0000) >> 16;
		int g = (color & 0xFF00) >> 8;
		int b = color & 0xFF;
		return new float[] { (float) r / 255.0F, (float) g / 255.0F, (float) b / 255.0F };
	}

	@Nullable
	@Override
	public float[] getBeaconColorMultiplier(BlockState state, LevelReader world, BlockPos pos, BlockPos beaconPos) {
		return decompColor(IMyaliteColorProvider.getColor(pos, myaliteS(), myaliteB()));
	}

	@Override
	public void dataGen(QuarkBlockStateProvider states) {
		ModelFile file = states.models().singleTexture(getRegistryName().getPath(), states.modLoc("block/cube_all_tinted"), "all", states.blockTexture(this));
		states.simpleBlock(this, file);
		states.simpleBlockItem(this, file);
	}

}
