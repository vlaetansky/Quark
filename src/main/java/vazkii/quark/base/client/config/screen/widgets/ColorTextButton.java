package vazkii.quark.base.client.config.screen.widgets;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ColorTextButton extends Button {

	private final int textColor;
	
	public ColorTextButton(int x, int y, int w, int h, Component text, int textColor, OnPress onClick) {
		super(x, y, w, h, text, onClick);
		this.textColor = textColor;
	}
	
	@Override
	public int getFGColor() {
		return textColor;
	}

}
