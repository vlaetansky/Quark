package vazkii.quark.base.network.message;

import net.minecraft.core.BlockPos;
import net.minecraftforge.network.NetworkEvent;
import vazkii.arl.network.IMessage;
import vazkii.quark.content.tweaks.module.SimpleHarvestModule;

public class HarvestMessage implements IMessage {

	private static final long serialVersionUID = -51788488328591145L;
	
	public BlockPos pos;

	public HarvestMessage() { }

	public HarvestMessage(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public boolean receive(NetworkEvent.Context context) {
		context.enqueueWork(() -> SimpleHarvestModule.click(context.getSender(), pos));
		return true;
	}

}
