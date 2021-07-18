package vazkii.quark.content.tools.module;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.handler.QuarkSounds;
import vazkii.quark.base.item.QuarkMusicDiscItem;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = ModuleCategory.TOOLS, hasSubscriptions = true)
public class AmbientMusicDiscModule extends QuarkModule {

	@Config public static boolean dropOnSpiderKill = true;
	
	List<Item> discs = new ArrayList<>();
	
	@Override
	public void construct() {
		disc(QuarkSounds.AMBIENT_DRIPS);
		disc(QuarkSounds.AMBIENT_OCEAN);
		disc(QuarkSounds.AMBIENT_RAIN);
		disc(QuarkSounds.AMBIENT_WIND);
		disc(QuarkSounds.AMBIENT_FIRE);
		disc(QuarkSounds.AMBIENT_CLOCK);
		disc(QuarkSounds.AMBIENT_CRICKETS);
		disc(QuarkSounds.AMBIENT_CHATTER);
	}
	
	void disc(SoundEvent sound) {
		String name = sound.getRegistryName().getPath().replaceAll(".+\\.", "");
		discs.add(new QuarkMusicDiscItem(15, () -> sound, name, this, true));
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onMobDeath(LivingDeathEvent event) {
		if(dropOnSpiderKill && event.getEntity() instanceof SpiderEntity && event.getSource().getTrueSource() instanceof SkeletonEntity) {
			Item item = discs.get(event.getEntity().world.rand.nextInt(discs.size()));
			event.getEntity().entityDropItem(item, 0);
		}
	}
	
}
