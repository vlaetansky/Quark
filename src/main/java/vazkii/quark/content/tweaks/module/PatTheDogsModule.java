package vazkii.quark.content.tweaks.module;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.content.mobs.entity.FoxhoundEntity;
import vazkii.quark.content.tweaks.ai.NuzzleGoal;
import vazkii.quark.content.tweaks.ai.WantLoveGoal;

/**
 * @author WireSegal
 * Created at 11:25 AM on 9/2/19.
 */
@LoadModule(category = ModuleCategory.TWEAKS, hasSubscriptions = true)
public class PatTheDogsModule extends QuarkModule {
    @Config(description = "How many ticks it takes for a dog to want affection after being pet/tamed; leave -1 to disable")
    public static int dogsWantLove = -1;

    @SubscribeEvent
    public void onWolfAppear(EntityJoinWorldEvent event) {
        if (dogsWantLove > 0 && event.getEntity() instanceof Wolf) {
            Wolf wolf = (Wolf) event.getEntity();
            boolean alreadySetUp = wolf.goalSelector.availableGoals.stream().anyMatch((goal) -> goal.getGoal() instanceof WantLoveGoal);

            if (!alreadySetUp) {
                wolf.goalSelector.addGoal(4, new NuzzleGoal(wolf, 0.5F, 16, 2, SoundEvents.WOLF_WHINE));
                wolf.goalSelector.addGoal(5, new WantLoveGoal(wolf, 0.2F));
            }
        }
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.EntityInteract event) {
        if(event.getTarget() instanceof Wolf) {
            Wolf wolf = (Wolf) event.getTarget();
            Player player = event.getPlayer();

            if(player.isDiscrete() && player.getMainHandItem().isEmpty()) {
                if(event.getHand() == InteractionHand.MAIN_HAND && WantLoveGoal.canPet(wolf)) {
                    if(player.level instanceof ServerLevel) {
                    	Vec3 pos = wolf.position();
                        ((ServerLevel) player.level).sendParticles(ParticleTypes.HEART, pos.x, pos.y + 0.5, pos.z, 1, 0, 0, 0, 0.1);
                        wolf.playSound(SoundEvents.WOLF_WHINE, 1F, 0.5F + (float) Math.random() * 0.5F);
                    } else player.swing(InteractionHand.MAIN_HAND);

                    WantLoveGoal.setPetTime(wolf);

                    if (wolf instanceof FoxhoundEntity && !player.isInWater() && !player.hasEffect(MobEffects.FIRE_RESISTANCE) && !player.isCreative())
                        player.setSecondsOnFire(5);
                }

                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onTame(AnimalTameEvent event) {
        if(event.getAnimal() instanceof Wolf) {
            Wolf wolf = (Wolf) event.getAnimal();
            WantLoveGoal.setPetTime(wolf);
        }
    }

}
