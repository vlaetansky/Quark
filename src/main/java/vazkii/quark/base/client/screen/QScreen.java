package vazkii.quark.base.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.Button.IPressable;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import vazkii.quark.base.client.config.ConfigCategory;

public abstract class QScreen extends Screen {
	
	private final Screen parent;
	
	public QScreen(Screen parent) {
		super(new StringTextComponent(""));
		this.parent = parent;
	}
	
	@Override
	public void render(MatrixStack mstack, int mouseX, int mouseY, float pticks) {
		super.render(mstack, mouseX, mouseY, pticks);
	}
	
	public void returnToParent(Button button) {
		minecraft.displayGuiScreen(parent);
	}
	
	public IPressable webLink(String url) {
		return b -> Util.getOSType().openURI(url);
	}
	
	public IPressable categoryLink(ConfigCategory category) {
		return b -> minecraft.displayGuiScreen(new QCategoryScreen(this, category));
	}
	

}
