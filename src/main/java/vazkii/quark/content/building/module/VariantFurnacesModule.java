package vazkii.quark.content.building.module;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.building.block.SoulFurnaceBlock;
import vazkii.quark.content.building.block.VariantFurnaceBlock;
import vazkii.quark.content.building.block.be.VariantFurnaceBlockEntity;

@LoadModule(category = ModuleCategory.BUILDING)
public class VariantFurnacesModule extends QuarkModule {

	public static BlockEntityType<VariantFurnaceBlockEntity> blockEntityType;
	
	@Override
	public void construct() {
		Block deepslateFurnace = new VariantFurnaceBlock("deepslate", this, Properties.copy(Blocks.DEEPSLATE));
		Block blackstoneFurnace = new SoulFurnaceBlock("blackstone", this, Properties.copy(Blocks.DEEPSLATE));
		
        blockEntityType = BlockEntityType.Builder.of(VariantFurnaceBlockEntity::new, deepslateFurnace, blackstoneFurnace).build(null);
        RegistryHelper.register(blockEntityType, "variant_furnace");
	}
	
}
