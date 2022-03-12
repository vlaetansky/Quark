package vazkii.quark.content.world.gen.structure.processor;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import vazkii.quark.content.world.module.BigDungeonModule;

public class BigDungeonWaterProcessor extends StructureProcessor {

	public BigDungeonWaterProcessor() {
		// NO-OP
	}

	@Override
	public StructureBlockInfo process(LevelReader worldReaderIn, BlockPos pos, BlockPos otherposidk, StructureBlockInfo otherinfoidk, StructureBlockInfo blockInfo, StructurePlaceSettings placementSettingsIn, StructureTemplate template) {
		if(blockInfo.state.getBlock() == Blocks.BARRIER)
			return new StructureBlockInfo(blockInfo.pos, Blocks.CAVE_AIR.defaultBlockState(), new CompoundTag());
		if(blockInfo.state.getProperties().contains(BlockStateProperties.WATERLOGGED) && blockInfo.state.getValue(BlockStateProperties.WATERLOGGED))
			return new StructureBlockInfo(blockInfo.pos, blockInfo.state.setValue(BlockStateProperties.WATERLOGGED, false), blockInfo.nbt);

		return blockInfo;
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return BigDungeonModule.WATER_PROCESSOR_TYPE;
	}

}

