package vazkii.quark.content.tools.module;

import net.minecraft.util.SoundEvent;
import vazkii.quark.base.handler.QuarkSounds;
import vazkii.quark.base.item.QuarkMusicDiscItem;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;

@LoadModule(category = ModuleCategory.TOOLS)
public class AmbientMusicDiscModule extends QuarkModule {

	@Override
	public void construct() {
		new QuarkMusicDiscItem(15, () -> QuarkSounds.AMBIENT_DRIPS, "drips", this, true);
		new QuarkMusicDiscItem(15, () -> QuarkSounds.AMBIENT_OCEAN, "ocean", this, true);
		new QuarkMusicDiscItem(15, () -> QuarkSounds.AMBIENT_RAIN,  "rain",  this, true);
		new QuarkMusicDiscItem(15, () -> QuarkSounds.AMBIENT_WIND,  "wind",  this, true);
	}
	
	SoundEvent getSound() {
		return QuarkSounds.AMBIENT_DRIPS;
	}
	
}
