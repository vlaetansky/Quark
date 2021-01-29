package vazkii.quark.content.building.module;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.DyeColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.building.block.StoolBlock;
import vazkii.quark.content.building.client.render.StoolEntityRenderer;
import vazkii.quark.content.building.entity.StoolEntity;

@LoadModule(category = ModuleCategory.BUILDING)
public class StoolsModule extends QuarkModule {

    public static EntityType<StoolEntity> stoolEntity;
	
	@Override
	public void construct() {
		for(DyeColor dye : DyeColor.values())
			new StoolBlock(this, dye);
		
		stoolEntity = EntityType.Builder.<StoolEntity>create(StoolEntity::new, EntityClassification.MISC)
                .size(6F / 16F, 0.5F)
                .trackingRange(3)
                .func_233608_b_(Integer.MAX_VALUE) // update interval
                .setShouldReceiveVelocityUpdates(false)
                .setCustomClientFactory((spawnEntity, world) -> new StoolEntity(stoolEntity, world))
                .build("stool");
        RegistryHelper.register(stoolEntity, "stool");
	}
	
    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientSetup() {
        RenderingRegistry.registerEntityRenderingHandler(stoolEntity, StoolEntityRenderer::new);
    }

}
