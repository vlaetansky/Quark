package vazkii.quark.content.tweaks.module;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = ModuleCategory.TWEAKS, hasSubscriptions = true)
public class PoisonPotatoUsageModule extends QuarkModule {

	private static final String TAG_POISONED = "quark:poison_potato_applied";

	@Config public static double chance = 0.1;
	@Config public static boolean poisonEffect = true;

	@SubscribeEvent
	public void onInteract(EntityInteract event) {
		if(event.getTarget() instanceof AgeableMob ageable && event.getItemStack().getItem() == Items.POISONOUS_POTATO) {
			if(ageable.isBaby() && !isEntityPoisoned(ageable)) {
				if(!event.getWorld().isClientSide) {
					Vec3 pos = ageable.position();
					if(ageable.level.random.nextDouble() < chance) {
						ageable.playSound(SoundEvents.GENERIC_EAT, 0.5f, 0.25f);
						ageable.level.addParticle(ParticleTypes.ENTITY_EFFECT, pos.x, pos.y, pos.z, 0.2, 0.8, 0);
						poisonEntity(ageable);
						if (poisonEffect)
							ageable.addEffect(new MobEffectInstance(MobEffects.POISON, 200));
					} else {
						ageable.playSound(SoundEvents.GENERIC_EAT, 0.5f, 0.5f + ageable.level.random.nextFloat() / 2);
						ageable.level.addParticle(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, 0, 0.1, 0);
					}

					if (!event.getPlayer().getAbilities().instabuild)
						event.getItemStack().shrink(1);

				} else event.getPlayer().swing(event.getHand());

			}
		}
	}

	@SubscribeEvent
	public void onEntityUpdate(LivingUpdateEvent event) {
		if(event.getEntity() instanceof Animal animal) {
			if(animal.isBaby() && isEntityPoisoned(animal))
				animal.setAge(-24000);
		}
	}

	private boolean isEntityPoisoned(Entity e) {
		return e.getPersistentData().getBoolean(TAG_POISONED);
	}

	private void poisonEntity(Entity e) {
		e.getPersistentData().putBoolean(TAG_POISONED, true);
	}

}
