package vazkii.quark.content.tweaks.module;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = ModuleCategory.TWEAKS, hasSubscriptions = true, subscribeOn = Dist.CLIENT)
public class UsesForCursesModule extends QuarkModule {

	private static boolean isEnabled;

	@Config
	public static boolean vanishPumpkinOverlay = true;

//	@Config
//	public static boolean bindArmorStandsWithPlayerHeads = true;


	@Override
	public void configChanged() {
		// Pass over to a static reference for easier computing the coremod hook
		isEnabled = this.enabled;
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void addArmorStandLayer(EntityRenderersEvent.AddLayers event) {
		// TODO: 4/6/22 armor stands rendering player model - not sure how to
	}

	public static boolean shouldHidePumpkinOverlay(ItemStack stack) {
		return isEnabled && vanishPumpkinOverlay &&
				stack.is(Blocks.CARVED_PUMPKIN.asItem()) &&
				EnchantmentHelper.getItemEnchantmentLevel(Enchantments.VANISHING_CURSE, stack) > 0;
	}

}
