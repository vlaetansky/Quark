package vazkii.quark.content.mobs.module;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.handler.EntityAttributeHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.world.EntitySpawnHandler;
import vazkii.quark.content.mobs.client.render.ForgottenRenderer;
import vazkii.quark.content.mobs.entity.ForgottenEntity;
import vazkii.quark.content.mobs.item.ForgottenHatItem;

@LoadModule(category = ModuleCategory.MOBS, hasSubscriptions = true)
public class ForgottenModule extends QuarkModule {

	public static EntityType<ForgottenEntity> forgottenType;

	public static Item forgotten_hat;

	@Config(description = "1 in this many Skeletons that spawn under the threshold are replaced with Forgotten.") 
	public double forgottenSpawnRate = 0.05;

	@Config public int maxHeightForSpawn = 20;

	@Override
	public void construct() {
		forgotten_hat = new ForgottenHatItem(this);

		forgottenType = EntityType.Builder.of(ForgottenEntity::new, MobCategory.MONSTER)
				.sized(0.7F, 2.4F)
				.clientTrackingRange(8)
				.setCustomClientFactory((spawnEntity, world) -> new ForgottenEntity(forgottenType, world))
				.build("forgotten");

		RegistryHelper.register(forgottenType, "forgotten");
		EntitySpawnHandler.addEgg(forgottenType, 0x969487, 0x3a3330, this, () -> true);
		
		EntityAttributeHandler.put(forgottenType, ForgottenEntity::registerAttributes);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		EntityRenderers.register(forgottenType, ForgottenRenderer::new);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onSkeletonSpawn(LivingSpawnEvent.CheckSpawn event) {
		LivingEntity entity = event.getEntityLiving();
		Result result = event.getResult();
		
		if(entity.getType() == EntityType.SKELETON && entity instanceof Mob && result != Result.DENY && entity.getY() < maxHeightForSpawn && entity.level.random.nextDouble() < forgottenSpawnRate) {
			Mob mob = (Mob) entity;

			if(result == Result.ALLOW || (mob.checkSpawnRules(entity.level, event.getSpawnReason()) && mob.checkSpawnObstruction(entity.level))) {
				ForgottenEntity forgotten = new ForgottenEntity(forgottenType, entity.level);
				Vec3 epos = entity.position();
	
				forgotten.absMoveTo(epos.x, epos.y, epos.z, entity.getYRot(), entity.getXRot());
				forgotten.prepareEquipment();
				entity.level.addFreshEntity(forgotten);
				event.setResult(Result.DENY);
			}
		}
	}

}
