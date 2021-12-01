package vazkii.quark.addons.oddities.client.screen;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import vazkii.quark.addons.oddities.container.BackpackContainer;
import vazkii.quark.addons.oddities.module.BackpackModule;
import vazkii.quark.base.Quark;
import vazkii.quark.base.network.QuarkNetwork;
import vazkii.quark.base.network.message.HandleBackpackMessage;

public class BackpackInventoryScreen extends InventoryScreen {
	
	private static final ResourceLocation BACKPACK_INVENTORY_BACKGROUND = new ResourceLocation(Quark.MOD_ID, "textures/misc/backpack_gui.png");
	
	private final Player player;
	private final Map<Button, Integer> buttonYs = new HashMap<>();
	
	private boolean closeHack = false;
	private static InventoryMenu oldContainer;
	
	public BackpackInventoryScreen(InventoryMenu backpack, Inventory inventory, Component component) {
		super(setBackpackContainer(inventory.player, backpack));
		
		this.player = inventory.player;
		setBackpackContainer(player, oldContainer);
	}
	
	public static Player setBackpackContainer(Player entity, InventoryMenu container) {
		oldContainer = entity.inventoryMenu;
		entity.inventoryMenu = container;
		
		return entity;
	}

	@Override
	public void init() {
		imageHeight = 224;
		super.init();
		
		/*for(Widget widget : buttons)
			if(widget instanceof ImageButton || widget.getClass().getName().contains("svenhjol.charm")) {
				widget.y -= 29;
				buttonYs.put((Button) widget, widget.y);
			}*/
	}

	@Override
	public void tick() {
		super.tick();

		buttonYs.forEach((button, y) -> button.y = y);
		
		if(!BackpackModule.isEntityWearingBackpack(player)) {
			ItemStack curr = player.inventory.getCarried();
			BackpackContainer.saveCraftingInventory(player);
			closeHack = true;
			QuarkNetwork.sendToServer(new HandleBackpackMessage(false));
			minecraft.setScreen(new InventoryScreen(player));
			player.inventory.setCarried(curr);
		}
	}
	
	@Override
	public void removed() {
		if(closeHack) {
			closeHack = false;
			return;
		}
			
		super.removed();
	}
	
	@Override 
	protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(BACKPACK_INVENTORY_BACKGROUND);
		int i = leftPos;
		int j = topPos;
		blit(stack, i, j, 0, 0, imageWidth, imageHeight);
		renderEntityInInventory(i + 51, j + 75, 30, i + 51 - mouseX, j + 75 - 50 - mouseY, minecraft.player);
		moveCharmsButtons();
	}

	private void moveCharmsButtons() {
		for(AbstractWidget widget : buttons) {
			//Charms buttons have a static Y pos, so use that to only focus on them.
			if(widget instanceof ImageButton && widget.y == height / 2 - 22) {
				((ImageButton) widget).setPosition(widget.x, widget.y - 29);
			}
		}
	}
	
}