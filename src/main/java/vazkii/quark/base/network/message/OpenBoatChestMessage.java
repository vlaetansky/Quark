package vazkii.quark.base.network.message;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.arl.network.IMessage;
import vazkii.quark.content.management.entity.ChestPassengerEntity;

public class OpenBoatChestMessage implements IMessage {

	private static final long serialVersionUID = 4454710003473142954L;

	@Override
	public boolean receive(NetworkEvent.Context context) {
		context.enqueueWork(() -> {
			Player player = context.getSender();

			if(player != null && player.isPassenger() && player.containerMenu == player.inventoryMenu) {
				Entity riding = player.getVehicle();
				if(riding instanceof Boat) {
					List<Entity> passengers = riding.getPassengers();
					for(Entity passenger : passengers) {
						if (passenger instanceof ChestPassengerEntity) {
							player.openMenu(new MenuProvider() {
								@Nonnull
								@Override
								public Component getDisplayName() {
									return new TranslatableComponent("container.chest");
								}

								@Nonnull
								@Override
								public AbstractContainerMenu createMenu(int id, @Nonnull Inventory inventory, @Nonnull Player player) {
									return ChestMenu.threeRows(id, inventory, (ChestPassengerEntity) passenger);
								}
							});

							break;
						}
					}
				}
			}
		});
		
		return true;
	}

}
