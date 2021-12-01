package vazkii.quark.content.building.module;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.building.block.StoolBlock;
import vazkii.quark.content.building.client.render.StoolEntityRenderer;
import vazkii.quark.content.building.entity.StoolEntity;

@LoadModule(category = ModuleCategory.BUILDING, hasSubscriptions = true)
public class StoolsModule extends QuarkModule {

    public static EntityType<StoolEntity> stoolEntity;
	
	@Override
	public void construct() {
		for(DyeColor dye : DyeColor.values())
			new StoolBlock(this, dye);
		
		stoolEntity = EntityType.Builder.<StoolEntity>of(StoolEntity::new, MobCategory.MISC)
                .sized(6F / 16F, 0.5F)
                .clientTrackingRange(3)
                .updateInterval(Integer.MAX_VALUE) // update interval
                .setShouldReceiveVelocityUpdates(false)
                .setCustomClientFactory((spawnEntity, world) -> new StoolEntity(stoolEntity, world))
                .build("stool");
        RegistryHelper.register(stoolEntity, "stool");
	}
	
	@SubscribeEvent
	public void itemUsed(RightClickBlock event) {
		if(event.getPlayer().isShiftKeyDown() && event.getItemStack().getItem() instanceof BlockItem && event.getFace() == Direction.UP) {
			BlockState state = event.getWorld().getBlockState(event.getPos());
			if(state.getBlock() instanceof StoolBlock)
				((StoolBlock) state.getBlock()).blockClicked(event.getWorld(), event.getPos());
		}
	}
	
    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientSetup() {
    	EntityRenderers.register(stoolEntity, StoolEntityRenderer::new);
    }

}
