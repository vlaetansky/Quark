package vazkii.quark.content.management.module;

import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.base.client.InventoryButtonHandler;
import vazkii.quark.base.client.InventoryButtonHandler.ButtonTargetType;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.network.QuarkNetwork;
import vazkii.quark.base.network.message.InventoryTransferMessage;
import vazkii.quark.content.management.client.gui.MiniInventoryButton;

@LoadModule(category = ModuleCategory.MANAGEMENT)
public class EasyTransferingModule extends QuarkModule {

	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		addButton(1, "insert", false);
		addButton(2, "extract", true);
	}

	@OnlyIn(Dist.CLIENT)
	private void addButton(int priority, String name, boolean restock) {
		InventoryButtonHandler.addButtonProvider(this, ButtonTargetType.CONTAINER_PLAYER_INVENTORY, priority,
				"transfer_" + name,
				(screen) -> QuarkNetwork.sendToServer(new InventoryTransferMessage(Screen.hasShiftDown(), restock)),
				(parent, x, y) -> new MiniInventoryButton(parent, priority, x, y, "quark.gui.button." + name,
						(b) -> QuarkNetwork.sendToServer(new InventoryTransferMessage(Screen.hasShiftDown(), restock)))
				.setTextureShift(Screen::hasShiftDown));
	}

}
