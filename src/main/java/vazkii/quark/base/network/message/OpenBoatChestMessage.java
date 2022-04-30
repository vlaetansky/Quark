package vazkii.quark.base.network.message;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraftforge.network.NetworkEvent;
import vazkii.arl.network.IMessage;
import vazkii.quark.content.management.entity.ChestPassenger;

import java.io.Serial;
import java.util.List;

public class OpenBoatChestMessage implements IMessage {

	@Serial
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
						if (passenger instanceof ChestPassenger chest) {
							player.openMenu(chest);

							break;
						}
					}
				}
			}
		});

		return true;
	}

}
