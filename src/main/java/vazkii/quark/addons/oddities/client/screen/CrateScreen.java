package vazkii.quark.addons.oddities.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import vazkii.quark.addons.oddities.container.CrateContainer;

public class CrateScreen extends ContainerScreen<CrateContainer> {

	private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

	final int inventoryRows;

	public CrateScreen(CrateContainer container, PlayerInventory inv, ITextComponent component) {
		super(container, inv, component);
		
		inventoryRows = container.numRows;
		ySize = 114 + this.inventoryRows * 18;
		playerInventoryTitleY = ySize - 94;
	}
	
	@Override
	protected void init() {
		super.init();
		
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		
		addButton(new Button(i - 22, j, 20, 20, new StringTextComponent("\u25B2"), b -> container.scroll(false, true)));
		addButton(new Button(i - 22, j + 22, 20, 20, new StringTextComponent("\u25BC"), b -> container.scroll(true, true)));
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.blit(matrixStack, i, j, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
		this.blit(matrixStack, i, j + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
	}

}
