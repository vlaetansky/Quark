package vazkii.quark.base.client.config;

import java.util.List;

import com.google.common.collect.ImmutableSet;

import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.loading.FMLPaths;
import vazkii.quark.base.Quark;
import vazkii.quark.base.client.config.gui.widget.QButton;
import vazkii.quark.base.handler.GeneralConfig;

@EventBusSubscriber(modid = Quark.MOD_ID, value = Dist.CLIENT)
public class QButtonHandler {

	@SubscribeEvent
	public static void onGuiInit(GuiScreenEvent.InitGuiEvent event) {
		Screen gui = event.getGui();
		
		if(GeneralConfig.enableQButton && (gui instanceof TitleScreen || gui instanceof PauseScreen)) {
			ImmutableSet<String> targets = GeneralConfig.qButtonOnRight 
					? ImmutableSet.of(I18n.get("fml.menu.modoptions"), I18n.get("menu.online"))
					: ImmutableSet.of(I18n.get("menu.options"), I18n.get("fml.menu.mods"));
					
			List<AbstractWidget> widgets = event.getWidgetList();
			for(AbstractWidget b : widgets)
				if(targets.contains(b.getMessage().getString())) {
					Button qButton = new QButton(b.x + (GeneralConfig.qButtonOnRight ? 103 : -24), b.y);
					event.addWidget(qButton);
					return;
				}
		}
	}
	
	public static void openFile() {
		Util.getPlatform().openFile(FMLPaths.CONFIGDIR.get().toFile());
	}
	
}
