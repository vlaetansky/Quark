package vazkii.quark.content.client.module;

import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.arl.util.ClientTicker;
import vazkii.quark.base.client.handler.ModKeybindHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = ModuleCategory.CLIENT, hasSubscriptions = true, subscribeOn = Dist.CLIENT)
public class AutoWalkKeybindModule extends QuarkModule {

	@Config public static boolean drawHud = true;
	@Config public static int hudHeight = 10;
	
	@OnlyIn(Dist.CLIENT)
	private KeyMapping keybind;

	private boolean autorunning;
	private boolean hadAutoJump;

	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		if(enabled)
			keybind = ModKeybindHandler.init("autorun", null, ModKeybindHandler.MISC_GROUP);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onMouseInput(InputEvent.MouseInputEvent event) {
		acceptInput();
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		acceptInput();
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void drawHUD(RenderGameOverlayEvent.Post event) {
		if(drawHud && autorunning && event.getType() == ElementType.ALL) {
			String message = I18n.get("quark.misc.autowalking");
			
			Minecraft mc = Minecraft.getInstance();
			int w = mc.font.width("OoO" + message + "oOo");
			
			Window window = event.getWindow();
			int x = (window.getGuiScaledWidth() - w) / 2;
			int y = hudHeight;
			
			String displayMessage = message;
			int dots = (ClientTicker.ticksInGame / 10) % 2;
			switch(dots) {
			case 0 -> displayMessage = "OoO " + message + " oOo";
			case 1 -> displayMessage = "oOo " + message + " OoO";
			}
			
			mc.font.drawShadow(event.getMatrixStack(), displayMessage, x, y, 0xFFFFFFFF);
		}
	}
	
	private void acceptInput() {
		Minecraft mc = Minecraft.getInstance();

		if(mc.options.keyUp.isDown()) {
			if(autorunning)
				mc.options.autoJump = hadAutoJump;
			
			autorunning = false;
		}
		
		else if(keybind.isDown()) {
			autorunning = !autorunning;

			if(autorunning) {
				hadAutoJump = mc.options.autoJump;
				mc.options.autoJump = true;
			} else mc.options.autoJump = hadAutoJump;
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onInput(MovementInputUpdateEvent event) {
		Minecraft mc = Minecraft.getInstance();
		if(mc.player != null && autorunning) {
			Input input = event.getInput();
			input.up = true;
			input.forwardImpulse = ((LocalPlayer) event.getPlayer()).isMovingSlowly() ? 0.3F : 1F;
		}
	}

}
