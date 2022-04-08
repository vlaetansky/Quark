package vazkii.quark.content.client.module;

import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.event.EntityRenderersEvent.AddLayers;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.content.tweaks.client.layer.ArmorStandFakePlayerLayer;

@LoadModule(category = ModuleCategory.CLIENT)
public class UsesForCursesModule extends QuarkModule {

	public static boolean staticEnabled;

	@Config
	public static boolean vanishPumpkinOverlay = true;

	@Config
	public static boolean bindArmorStandsWithPlayerHeads = true;

	@Override
	public void configChanged() {
		staticEnabled = enabled;
	}

	@Override
	public void modelLayers(AddLayers event) {
		ArmorStandRenderer render = event.getRenderer(EntityType.ARMOR_STAND);
		render.addLayer(new ArmorStandFakePlayerLayer<>(render, event.getEntityModels()));
	}

	public static boolean shouldHidePumpkinOverlay(ItemStack stack) {
		return staticEnabled && vanishPumpkinOverlay &&
				stack.is(Blocks.CARVED_PUMPKIN.asItem()) &&
				EnchantmentHelper.getItemEnchantmentLevel(Enchantments.VANISHING_CURSE, stack) > 0;
	}

}
