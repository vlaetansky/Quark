package vazkii.quark.base.client.config;

import java.awt.Color;
import java.util.Calendar;
import java.util.List;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.loading.FMLPaths;
import vazkii.arl.util.ClientTicker;
import vazkii.quark.base.Quark;
import vazkii.quark.base.client.config.gui.QHomeScreen;
import vazkii.quark.base.handler.ContributorRewardHandler;
import vazkii.quark.base.handler.GeneralConfig;
import vazkii.quark.base.handler.MiscUtil;

@EventBusSubscriber(modid = Quark.MOD_ID, value = Dist.CLIENT)
public class QButtonHandler {

	@SubscribeEvent
	public static void onGuiInit(GuiScreenEvent.InitGuiEvent event) {
		Screen gui = event.getGui();
		
		if(GeneralConfig.enableQButton && (gui instanceof MainMenuScreen || gui instanceof IngameMenuScreen)) {
			ImmutableSet<String> targets = GeneralConfig.qButtonOnRight 
					? ImmutableSet.of(I18n.format("fml.menu.modoptions"), I18n.format("menu.online"))
					: ImmutableSet.of(I18n.format("menu.options"), I18n.format("fml.menu.mods"));
					
			List<Widget> widgets = event.getWidgetList();
			for(Widget b : widgets)
				if(targets.contains(b.getMessage().getString())) {
					Button qButton = new QButton(b.x + (GeneralConfig.qButtonOnRight ? 103 : -24), b.y);
					event.addWidget(qButton);
					return;
				}
		}
	}
	
	public static void openFile() {
		Util.getOSType().openFile(FMLPaths.CONFIGDIR.get().toFile());
	}
	
	private static class QButton extends Button {

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
	
}
