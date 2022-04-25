package vazkii.quark.content.mobs.item;

import net.minecraft.world.item.ItemStack;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.item.QuarkItem;
import vazkii.quark.base.module.QuarkModule;

import javax.annotation.Nonnull;

/**
 * @author WireSegal
 * Created at 10:55 PM on 4/24/22.
 */
public class FrogLegItem extends QuarkItem {
	public FrogLegItem(String regname, QuarkModule module, Properties properties) {
		super(regname, module, properties);
	}

	@Nonnull
	@Override
	public String getDescriptionId(@Nonnull ItemStack stack) {
		String id = super.getDescriptionId(stack);
		if (ItemNBTHelper.getBoolean(stack, "sus", false))
			return id + "_maybe";
		return id;
	}
}
