package vazkii.quark.content.management.module;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.network.QuarkNetwork;
import vazkii.quark.base.network.message.OpenBoatChestMessage;
import vazkii.quark.content.management.client.render.ChestPassengerRenderer;
import vazkii.quark.content.management.entity.ChestPassengerEntity;

@LoadModule(category = ModuleCategory.MANAGEMENT, hasSubscriptions = true)
public class ChestsInBoatsModule extends QuarkModule {

	public static EntityType<ChestPassengerEntity> chestPassengerEntityType;

	private static Tag<Item> boatableChestsTag;
	
	@Override
	public void construct() {
		chestPassengerEntityType = EntityType.Builder.<ChestPassengerEntity>of(ChestPassengerEntity::new, MobCategory.MISC)
				.sized(0.8F, 0.8F)
				.updateInterval(128) // update interval
				.setCustomClientFactory((spawnEntity, world) -> new ChestPassengerEntity(chestPassengerEntityType, world))
				.build("chest_passenger");
		RegistryHelper.register(chestPassengerEntityType, "chest_passenger");
	}
	
    @Override
    public void setup() {
    	boatableChestsTag = ItemTags.createOptional(new ResourceLocation(Quark.MOD_ID, "boatable_chests"));
    }

	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		RenderingRegistry.registerEntityRenderingHandler(chestPassengerEntityType, ChestPassengerRenderer::new);
	}

	@SubscribeEvent
	public void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
		Entity target = event.getTarget();
		Player player = event.getPlayer();

		if(target instanceof Boat && target.getPassengers().isEmpty()) {
			InteractionHand hand = InteractionHand.MAIN_HAND;
			ItemStack stack = player.getMainHandItem();
			if(!isChest(stack)) {
				stack = player.getOffhandItem();
				hand = InteractionHand.OFF_HAND;
			}

			if(isChest(stack)) {
				Level world = event.getWorld();
				
				if(!event.getWorld().isClientSide) {
					ItemStack chestStack = stack.copy();
					chestStack.setCount(1);
					if (!player.isCreative())
						stack.shrink(1);

					ChestPassengerEntity passenger = new ChestPassengerEntity(world, chestStack);
					Vec3 pos = target.position();
					passenger.setPos(pos.x, pos.y, pos.z);
					passenger.yRot = target.yRot;
					passenger.startRiding(target, true);
					world.addFreshEntity(passenger);
				}
				
				player.swing(hand);
				event.setCancellationResult(InteractionResult.SUCCESS);
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	@OnlyIn(Dist.CLIENT)
	public void onOpenGUI(GuiOpenEvent event) {
		Player player = Minecraft.getInstance().player;
		if(player != null && event.getGui() instanceof InventoryScreen && player.isPassenger()) {
			Entity riding = player.getVehicle();
			if(riding instanceof Boat) {
				List<Entity> passengers = riding.getPassengers();
				for(Entity passenger : passengers)
					if(passenger instanceof ChestPassengerEntity) {
						QuarkNetwork.sendToServer(new OpenBoatChestMessage());
						event.setCanceled(true);
						return;
					}
			}
		}
	}
	
	private boolean isChest(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem().is(boatableChestsTag);
	}
}
