package vazkii.quark.base.network.message;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import vazkii.arl.network.IMessage;
import vazkii.quark.addons.oddities.container.CrateContainer;

public class ScrollCrateMessage implements IMessage {

	private static final long serialVersionUID = -921358009630134620L;
	
	public boolean down;
	
	public ScrollCrateMessage() { }
	
	public ScrollCrateMessage(boolean down) {
		this.down = down;
	}
	
	@Override
	public boolean receive(Context context) {
		context.enqueueWork(() -> {
			ServerPlayer player = context.getSender();
			AbstractContainerMenu container = player.containerMenu;
			
			if(container instanceof CrateContainer)
				((CrateContainer) container).scroll(down, false);
		});
		
		return true;
	}

}
