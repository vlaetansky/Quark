package vazkii.quark.addons.oddities.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;

public class MatrixEnchantingPlusButton extends Button {

	public MatrixEnchantingPlusButton(int x, int y, OnPress onPress) {
		super(x, y, 50, 12, new TextComponent(""), onPress);
	}
	
	@Override
	public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		boolean hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
		if(!visible)
			return;
		
		Minecraft.getInstance().textureManager.bind(MatrixEnchantingScreen.BACKGROUND);
		int u = 0;
		int v = 177;
		
		if(!active)
			v += 12;
		else if(hovered)
			v += 24;

		RenderSystem.color3f(1F, 1F, 1F);
		blit(stack, x, y, u, v, width, height);
	}

}
