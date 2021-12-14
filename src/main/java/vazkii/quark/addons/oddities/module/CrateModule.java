package vazkii.quark.addons.oddities.module;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeMenuType;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.addons.oddities.block.CrateBlock;
import vazkii.quark.addons.oddities.block.be.CrateBlockEntity;
import vazkii.quark.addons.oddities.client.screen.CrateScreen;
import vazkii.quark.addons.oddities.inventory.CrateMenu;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = ModuleCategory.ODDITIES)
public class CrateModule extends QuarkModule {

    public static BlockEntityType<CrateBlockEntity> crateType;
	public static MenuType<CrateMenu> containerType;
	
	public static Block crate;
	
	@Config public static int maxItems = 640;

	@Override
	public void construct() {
		crate = new CrateBlock(this);
		
		containerType = IForgeMenuType.create(CrateMenu::fromNetwork);
		RegistryHelper.register(containerType, "crate");
		
		crateType = BlockEntityType.Builder.of(CrateBlockEntity::new, crate).build(null);
		RegistryHelper.register(crateType, "crate");
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		MenuScreens.register(containerType, CrateScreen::new);
	}
	
}
