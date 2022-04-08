package vazkii.quark.content.experimental.module;

import java.util.List;

import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;

@LoadModule(category = ModuleCategory.EXPERIMENTAL, enabledByDefault = false, hasSubscriptions = true)
public class GrabChickensModule extends QuarkModule {

	private static boolean staticEnabled;
	
	@Override
	public void configChanged() {
		staticEnabled = enabled;
	}
	
	@SubscribeEvent
	public void playerInteract(PlayerInteractEvent.EntityInteract event) {
		Entity target = event.getTarget();
		Player player = event.getPlayer();
		Level level = event.getWorld();
		
		if(staticEnabled && event.getHand() == InteractionHand.MAIN_HAND && !player.isCrouching() && target.getType() == EntityType.CHICKEN) {
			List<Entity> passengers = player.getPassengers();
			
			boolean changed = false;
			
			if(passengers.contains(target)) {
				if(!level.isClientSide)
					target.stopRiding();
				
				changed = true;
			} else if(passengers.isEmpty()) {
				if(!level.isClientSide)
					target.startRiding(player, false);
				
				changed = true;
			}
			
			if(changed) {
				if(level instanceof ServerLevel slevel)
					slevel.getChunkSource().chunkMap.broadcast(target, new ClientboundSetPassengersPacket(player));
				else player.swing(InteractionHand.MAIN_HAND);
			}
		}
	}
	
	@SubscribeEvent
	public void playerTick(PlayerTickEvent event) {
		Player player = event.player;
		if(player.hasPassenger(e -> e.getType() == EntityType.CHICKEN)) {
			MobEffectInstance effect = player.getEffect(MobEffects.SLOW_FALLING);
			if(effect == null)
				player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 5, 0, true, false));
		}
	}
	
}
