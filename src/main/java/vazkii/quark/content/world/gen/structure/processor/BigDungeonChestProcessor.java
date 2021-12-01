package vazkii.quark.content.world.gen.structure.processor;

import java.util.Random;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import vazkii.quark.content.world.gen.structure.BigDungeonStructure;
import vazkii.quark.content.world.module.BigDungeonModule;

public class BigDungeonChestProcessor extends StructureProcessor {
	
    public BigDungeonChestProcessor() { 
    	// NO-OP
    }
    
    @Override
    public StructureBlockInfo process(LevelReader worldReaderIn, BlockPos pos, BlockPos otherposidk, StructureBlockInfo p_215194_3_, StructureBlockInfo blockInfo, StructurePlaceSettings placementSettingsIn, StructureTemplate template) {
    	if(blockInfo.state.getBlock() instanceof ChestBlock) {
    		Random rand = placementSettingsIn.getRandom(blockInfo.pos);
    		if(rand.nextDouble() > BigDungeonModule.chestChance)
	            return new StructureBlockInfo(blockInfo.pos, Blocks.CAVE_AIR.defaultBlockState(), new CompoundTag());
    		if (blockInfo.nbt.getString("id").equals("minecraft:chest")) {
    			blockInfo.nbt.putString("LootTable", BigDungeonModule.lootTable);
    			blockInfo.nbt.putLong("LootTableSeed", rand.nextLong());
    			return new StructureBlockInfo(blockInfo.pos, blockInfo.state, blockInfo.nbt);
    		}
    	}
    	
    	return blockInfo;
    }
    
	@Override
	protected StructureProcessorType<?> getType() {
		return BigDungeonStructure.CHEST_PROCESSOR_TYPE;
	}

}
