package vazkii.quark.base.client.config.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public class IconButton extends Button {

	private final ItemStack icon;
	
	public IconButton(int x, int y, int w, int h, ITextComponent text, ItemStack icon, IPressable onClick) {
		super(x, y, w, h, text, onClick);
		this.icon = icon;
	}
	
	@Override
	public void render(MatrixStack mstack, int mouseX, int mouseY, float pticks) {
		super.render(mstack, mouseX, mouseY, pticks);
		Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(icon, x + 5, y + 2);
	}

}
