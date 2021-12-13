package vazkii.quark.base.client.config.screen.widgets;

import java.util.Arrays;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import vazkii.quark.base.client.handler.TopLayerTooltipHandler;

public class IconButton extends Button {

	private final ItemStack icon;
	final Component text;

	public IconButton(int x, int y, int w, int h, Component text, ItemStack icon, OnPress onClick) {
		super(x, y, w, h, new TextComponent(""), onClick);
		this.icon = icon;
		this.text = text;
	}

	@Override
	public void render(PoseStack mstack, int mouseX, int mouseY, float pticks) {
		super.render(mstack, mouseX, mouseY, pticks);

		if(!active && mouseX >= x && mouseY >= y && mouseX < (x + width) && mouseY < (y + height))
			TopLayerTooltipHandler.setTooltip(Arrays.asList(I18n.get("quark.gui.config.missingaddon")), mouseX, mouseY);

		Minecraft mc = Minecraft.getInstance();
		mc.getItemRenderer().renderGuiItem(icon, x + 5, y + 2);
		
		int iconPad = (16 + 5) / 2;
		drawCenteredString(mstack, mc.font, text, x + width / 2 + iconPad, y + (height - 8) / 2, getFGColor());
	}

}
