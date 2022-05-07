package vazkii.quark.base.network.message;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;
import vazkii.arl.network.IMessage;
import vazkii.quark.content.tweaks.module.SimpleHarvestModule;

import java.io.Serial;

public class HarvestMessage implements IMessage {

	@Serial
	private static final long serialVersionUID = -51788488328591145L;

	public BlockPos pos;
	public InteractionHand hand;

	public HarvestMessage() { }

	public HarvestMessage(BlockPos pos, InteractionHand hand) {
		this.pos = pos;
		this.hand = hand;
	}

	@Override
	public boolean receive(NetworkEvent.Context context) {
		context.enqueueWork(() -> SimpleHarvestModule.click(context.getSender(), hand, pos));
		return true;
	}

}
