package vazkii.quark.content.world.gen.structure.processor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import vazkii.quark.content.world.module.BigDungeonModule;

import javax.annotation.Nonnull;

public class BigDungeonWaterProcessor extends StructureProcessor {

	public BigDungeonWaterProcessor() {
		// NO-OP
	}

	@Override
	public StructureBlockInfo process(@Nonnull LevelReader worldReaderIn, @Nonnull BlockPos pos, @Nonnull BlockPos otherposidk, @Nonnull StructureBlockInfo otherinfoidk, StructureBlockInfo blockInfo, @Nonnull StructurePlaceSettings placementSettingsIn, StructureTemplate template) {
		if(blockInfo.state.getProperties().contains(BlockStateProperties.WATERLOGGED) && blockInfo.state.getValue(BlockStateProperties.WATERLOGGED))
			return new StructureBlockInfo(blockInfo.pos, blockInfo.state.setValue(BlockStateProperties.WATERLOGGED, false), blockInfo.nbt);

		return blockInfo;
	}

	@Nonnull
	@Override
	protected StructureProcessorType<?> getType() {
		return BigDungeonModule.WATER_PROCESSOR_TYPE;
	}

}

