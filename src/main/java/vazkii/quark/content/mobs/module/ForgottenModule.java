package vazkii.quark.content.mobs.module;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.world.EntitySpawnHandler;
import vazkii.quark.content.mobs.client.render.ForgottenRenderer;
import vazkii.quark.content.mobs.entity.ForgottenEntity;

@LoadModule(category = ModuleCategory.MOBS, hasSubscriptions = true)
public class ForgottenModule extends QuarkModule {

	public static EntityType<ForgottenEntity> forgottenType;
	
	@Override
	public void construct() {
		forgottenType = EntityType.Builder.create(ForgottenEntity::new, EntityClassification.MONSTER)
				.size(0.7F, 2.4F)
				.setTrackingRange(80)
				.setUpdateInterval(3)
				.setCustomClientFactory((spawnEntity, world) -> new ForgottenEntity(forgottenType, world))
				.build("forgotten");
		
		RegistryHelper.register(forgottenType, "forgotten");
        EntitySpawnHandler.addEgg(forgottenType, 0x969487, 0x3a3330, this, () -> true);
	}
	
	@Override
	public void setup() {
		super.setup();
		
		GlobalEntityTypeAttributes.put(forgottenType, AbstractSkeletonEntity.registerAttributes().create());
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		RenderingRegistry.registerEntityRenderingHandler(forgottenType, ForgottenRenderer::new);
	}
	
}
