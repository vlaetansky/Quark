package vazkii.quark.content.world.gen.structure.processor;

import java.util.Random;

import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.level.BaseSpawner;
import vazkii.quark.content.world.gen.structure.BigDungeonStructure;

public class BigDungeonSpawnerProcessor extends StructureProcessor {
	
    public BigDungeonSpawnerProcessor() { 
    	// NO-OP
    }
    
    @Override
    public StructureBlockInfo process(LevelReader worldReaderIn, BlockPos pos, BlockPos otherposidk, StructureBlockInfo p_215194_3_, StructureBlockInfo blockInfo, StructurePlaceSettings placementSettingsIn, StructureTemplate template) {
    	if(blockInfo.state.getBlock() instanceof SpawnerBlock) {
    		Random rand = placementSettingsIn.getRandom(blockInfo.pos);
    		BlockEntity tile = BlockEntity.loadStatic(blockInfo.state, blockInfo.nbt);
    		
    		if(tile instanceof SpawnerBlockEntity) {
    			SpawnerBlockEntity spawner = (SpawnerBlockEntity) tile;
    			BaseSpawner logic = spawner.getSpawner();
    			
    			double val = rand.nextDouble();
    			if(val > 0.95)
    				logic.setEntityId(EntityType.CREEPER);
    			else if(val > 0.5)
    				logic.setEntityId(EntityType.SKELETON);
    			else logic.setEntityId(EntityType.ZOMBIE);
    			
    			CompoundTag nbt = new CompoundTag();
    			spawner.save(nbt);
    			return new StructureBlockInfo(blockInfo.pos, blockInfo.state, nbt);
    		}
    	}
    	
    	return blockInfo;
    }
    
	@Override
	protected StructureProcessorType<?> getType() {
		return BigDungeonStructure.SPAWN_PROCESSOR_TYPE;
	}

}
