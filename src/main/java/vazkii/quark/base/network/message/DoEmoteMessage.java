package vazkii.quark.base.network.message;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import vazkii.arl.network.IMessage;
import vazkii.quark.content.tweaks.client.emote.EmoteHandler;

public class DoEmoteMessage implements IMessage {

	private static final long serialVersionUID = -7952633556330869633L;
	
	public String emote;
	public UUID playerUUID;
	public int tier;
	
	public DoEmoteMessage() { }
	
	public DoEmoteMessage(String emote, UUID playerUUID, int tier) {
		this.emote = emote;
		this.playerUUID = playerUUID;
		this.tier = tier;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean receive(Context context) {
		context.enqueueWork(() -> {
			Level world = Minecraft.getInstance().level;
			Player player = world.getPlayerByUUID(playerUUID);
			EmoteHandler.putEmote(player, emote, tier);
		});
		
		return true;
	}

}
