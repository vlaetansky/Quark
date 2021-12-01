package vazkii.quark.content.tweaks.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import net.minecraft.client.gui.components.Button.OnPress;

public class TranslucentButton extends Button {

	public TranslucentButton(int xIn, int yIn, int widthIn, int heightIn, Component text, OnPress onPress) {
		super(xIn, yIn, widthIn, heightIn, text, onPress);
	}
	
	@Override
	public void blit(PoseStack stack, int x, int y, int textureX, int textureY, int width, int height) {
		fill(stack, x, y, x + width, y + height, Integer.MIN_VALUE);
	}

}
