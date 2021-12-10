package vazkii.quark.content.management.module;

import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = ModuleCategory.MANAGEMENT)
public class ExpandedItemInteractionsModule extends QuarkModule {

	@Config public static boolean enableArmorInteraction = true;
	@Config public static boolean enableShulkerBoxInteraction = true;
	@Config public static boolean enableLavaInteraction = true;

	private static boolean staticEnabled = false;

	public static boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
		if(!staticEnabled)
			return false;
		
		System.out.println("STACK ON OTHER");
		return false; // TODO
	}

	public static boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack incoming, Slot slot, ClickAction action, Player player, SlotAccess accessor) {
		if(!staticEnabled)
			return false;

		System.out.println("STACK ON ME");
		return false; // TODO
	}

	@Override
	public void configChanged() {
		staticEnabled = configEnabled;
	}

}
