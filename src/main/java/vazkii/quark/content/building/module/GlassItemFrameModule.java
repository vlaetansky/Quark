package vazkii.quark.content.building.module;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ForgeModelBakery;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.content.building.client.render.entity.GlassItemFrameRenderer;
import vazkii.quark.content.building.entity.GlassItemFrame;
import vazkii.quark.content.building.item.QuarkItemFrameItem;

/**
 * @author WireSegal
 * Created at 11:00 AM on 8/25/19.
 */
@LoadModule(category = ModuleCategory.BUILDING)
public class GlassItemFrameModule extends QuarkModule {

	@Config public static boolean glassItemFramesUpdateMaps = true;
	
    public static Item glassFrame;
    public static Item glowingGlassFrame;

    public static EntityType<GlassItemFrame> glassFrameEntity;

    @Override
    public void construct() {
        glassFrameEntity = EntityType.Builder.<GlassItemFrame>of(GlassItemFrame::new, MobCategory.MISC)
                .sized(0.5F, 0.5F)
                .clientTrackingRange(10)
                .updateInterval(Integer.MAX_VALUE) // update interval
                .setShouldReceiveVelocityUpdates(false)
                .setCustomClientFactory((spawnEntity, world) -> new GlassItemFrame(glassFrameEntity, world))
                .build("glass_frame");
        RegistryHelper.register(glassFrameEntity, "glass_frame");

        glassFrame = new QuarkItemFrameItem("glass_item_frame", this, GlassItemFrame::new);
        glowingGlassFrame = new QuarkItemFrameItem("glowing_glass_item_frame", this, 
        		(w, p, d) -> {
        			GlassItemFrame e = new GlassItemFrame(w, p, d);
        			e.getEntityData().set(GlassItemFrame.IS_SHINY, true);
        			return e;
        		});
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientSetup() {
        EntityRenderers.register(glassFrameEntity, GlassItemFrameRenderer::new);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void modelRegistry() {
        ForgeModelBakery.addSpecialModel(new ModelResourceLocation(new ResourceLocation(Quark.MOD_ID, "glass_frame"), "inventory")); 
    }
}
