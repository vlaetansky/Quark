package vazkii.quark.content.tweaks.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.Dimension;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.handler.QuarkSounds;
import vazkii.quark.base.item.QuarkMusicDiscItem;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;

@LoadModule(category = ModuleCategory.TWEAKS, hasSubscriptions = true, subscribeOn = Dist.CLIENT)
public class EnderdragonMusicModule extends QuarkModule {

	public static QuarkMusicDiscItem tedium;

	private boolean isFightingDragon;
	private int delay;
	private SimpleSound sound;

	@Override
	public void construct() {
		tedium = new QuarkMusicDiscItem(14, () -> QuarkSounds.MUSIC_TEDIUM, "tedium", this);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void tick(ClientTickEvent event) {
		if(event.phase == Phase.END) {
			boolean wasFightingDragon = isFightingDragon;

			Minecraft mc = Minecraft.getInstance();
			isFightingDragon = mc.world != null 
					&& mc.world.getDimensionKey().getLocation().equals(Dimension.THE_END.getLocation())
					&& mc.ingameGUI.getBossOverlay().shouldPlayEndBossMusic();
			
			final int targetDelay = 50;
			
			if(isFightingDragon) {
				if(delay == targetDelay) {
					sound = SimpleSound.music(QuarkSounds.MUSIC_TEDIUM);
					mc.getSoundHandler().playDelayed(sound, 0);
					mc.ingameGUI.func_238451_a_(tedium.getDescription());
				}

				double x = mc.player.getPosX();
				double z = mc.player.getPosZ();

				if(mc.currentScreen == null && ((x*x) + (z*z)) < 3000) // is not in screen and within island
					delay++;
				
			} else if(wasFightingDragon && sound != null) {
				mc.getSoundHandler().stop(sound);
				delay = 0;
				sound = null;
			}
		}


	}

}
