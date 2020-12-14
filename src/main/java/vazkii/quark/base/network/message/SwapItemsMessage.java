package vazkii.quark.base.network.message;

import net.minecraftforge.fml.network.NetworkEvent.Context;
import vazkii.arl.network.IMessage;
import vazkii.quark.content.management.module.FToSwitchModule;

public class SwapItemsMessage implements IMessage {

	private static final long serialVersionUID = -3490303586245368973L;

	public int index;
	
	public SwapItemsMessage() { }
	
	public SwapItemsMessage(int index) { 
		this.index = index;
	}
	
	@Override
	public boolean receive(Context context) {
		context.enqueueWork(() -> FToSwitchModule.switchItems(context.getSender(), index));
		return true;
	}

}
