package vazkii.quark.content.building.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.datagen.QuarkBlockStateProvider;
import vazkii.quark.base.module.QuarkModule;

public class BambooMatBlock extends QuarkBlock {

	public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING_HOPPER;

	public BambooMatBlock(String name, QuarkModule module) {
		this(name, module, CreativeModeTab.TAB_BUILDING_BLOCKS);
	}

	public BambooMatBlock(String name, QuarkModule module, CreativeModeTab tab) {
		super(name, module, tab,
				Block.Properties.of(Material.BAMBOO, MaterialColor.COLOR_YELLOW)
				.strength(0.5F)
				.sound(SoundType.BAMBOO));

		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		Direction dir = ctx.getHorizontalDirection();
		if(ctx.getPlayer().getXRot() > 70)
			dir = Direction.DOWN;

		if(dir != Direction.DOWN) {
			Direction opposite = dir.getOpposite();
			BlockPos target = ctx.getClickedPos().relative(opposite);
			BlockState state = ctx.getLevel().getBlockState(target);

			if(state.getBlock() != this || state.getValue(FACING) != opposite) {
				target = ctx.getClickedPos().relative(dir);
				state = ctx.getLevel().getBlockState(target);

				if(state.getBlock() == this && state.getValue(FACING) == dir)
					dir = opposite;
			}
		}

		return defaultBlockState().setValue(FACING, dir);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public void dataGen(QuarkBlockStateProvider states) {
		ModelFile baseModel = states.models().getExistingFile(states.blockTexture(this));
		ModelFile fullModel = states.models().getExistingFile(states.blockTexture(this, "_full"));

		states.getVariantBuilder(this)
				.partialState().with(FACING, Direction.EAST).setModels(new ConfiguredModel(baseModel, 0, 90, false))
				.partialState().with(FACING, Direction.NORTH).setModels(new ConfiguredModel(baseModel, 0, 0, false))
				.partialState().with(FACING, Direction.SOUTH).setModels(new ConfiguredModel(baseModel, 0, 180, false))
				.partialState().with(FACING, Direction.WEST).setModels(new ConfiguredModel(baseModel, 0, 270, false))
				.partialState().with(FACING, Direction.DOWN).setModels(new ConfiguredModel(fullModel));
		states.simpleBlockItem(this, baseModel);
	}
}
