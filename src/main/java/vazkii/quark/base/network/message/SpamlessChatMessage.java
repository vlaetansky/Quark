/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [02/04/2016, 17:44:30 (GMT)]
 */
package vazkii.quark.base.network.message;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import vazkii.arl.network.IMessage;
import vazkii.quark.base.network.QuarkNetwork;

import java.io.Serial;

public class SpamlessChatMessage implements IMessage {

	@Serial
	private static final long serialVersionUID = -4716987873031723456L;

	public Component message;
	public int id;

	public SpamlessChatMessage() { }

	public SpamlessChatMessage(Component message, int id) {
		this.message = message;
		this.id = id;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean receive(NetworkEvent.Context context) {
		context.enqueueWork(() -> {
			Gui gui = Minecraft.getInstance().gui;
			gui.getChat().addMessage(message, id, gui.getGuiTicks(), false); // print message and delete if same ID, called by printChatMessageWithOptionalDeletion
		});

		return true;
	}

	public static void sendToPlayer(Player player, int id, Component component) {
		if (player instanceof ServerPlayer serverPlayer)
			QuarkNetwork.sendToPlayer(new SpamlessChatMessage(component, id), serverPlayer);
	}

}
