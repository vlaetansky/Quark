package vazkii.quark.base.client.config.gui;

import java.awt.Color;
import java.util.Calendar;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import vazkii.arl.util.ClientTicker;
import vazkii.quark.base.client.config.IngameConfigHandler;
import vazkii.quark.base.handler.ContributorRewardHandler;
import vazkii.quark.base.handler.MiscUtil;

public class QButton extends Button {

	private final boolean gay;
	
	public QButton(int x, int y) {
		super(x, y, 20, 20, new StringTextComponent("q"), QButton::click);
		gay = Calendar.getInstance().get(Calendar.MONTH) + 1 == 6;
	}
	
	@Override
	public int getFGColor() {
		return gay ? Color.HSBtoRGB((ClientTicker.total / 200F), 1F, 1F) : 0x48DDBC;
	}
	
	@Override
	public void renderButton(MatrixStack mstack, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
		super.renderButton(mstack, p_renderButton_1_, p_renderButton_2_, p_renderButton_3_);
		
		if(ContributorRewardHandler.localPatronTier > 0) {
			RenderSystem.color3f(1F, 1F, 1F);
			int tier = Math.min(4, ContributorRewardHandler.localPatronTier);
			int u = 256 - tier * 9;
			int v = 26;
			
			Minecraft.getInstance().textureManager.bindTexture(MiscUtil.GENERAL_ICONS);
			blit(mstack, x - 2, y - 2, u, v, 9, 9);
		}
	}
	
	public static void click(Button b) {
		Minecraft.getInstance().displayGuiScreen(new QHomeScreen(Minecraft.getInstance().currentScreen));
		IngameConfigHandler.INSTANCE.debug();
	}
	
}