package vazkii.quark.base.client.config.gui;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.MainWindow;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import vazkii.quark.base.client.config.ConfigCategory;

public class QCategoryScreen extends QScreen {

	public final ConfigCategory category;

	private List<Widget> vigiledWidgets = new LinkedList<>();
	
	private ConfigElementList elementList;
	private String breadcrumbs;
	private Button resetButton;
	
	public QCategoryScreen(Screen parent, ConfigCategory category) {
		super(parent);
		this.category = category;
	}
	
	@Override
	protected void init() {
		super.init();
		
		breadcrumbs = category.getName();
		ConfigCategory currCategory = category.getParent();
		while(currCategory != null) {
			breadcrumbs = String.format("%s > %s", currCategory.getName(), breadcrumbs);
			currCategory = currCategory.getParent();
		}
		breadcrumbs = String.format("> %s", breadcrumbs);
		
		elementList = new ConfigElementList(this, w -> {
			children.add(w);
			vigiledWidgets.add(w);
			if(w instanceof Button)
				addButton(w);
		});
		
		children.add(elementList);
		
		int pad = 3;
		int bWidth = 121;
		int left = (width - (bWidth + pad) * 3) / 2;
		int vStart = height - 30;
		
		addButton(new Button(left, vStart, bWidth, 20, new TranslationTextComponent("quark.gui.config.default"), b -> category.reset(true)));
		addButton(resetButton = new Button(left + bWidth + pad, vStart, bWidth, 20, new TranslationTextComponent("quark.gui.config.discard"), b -> category.reset(false)));
		addButton(new Button(left + (bWidth + pad) * 2, vStart, bWidth, 20, new TranslationTextComponent("gui.done"), this::returnToParent));
	}
	
	@Override
	public void tick() {
		super.tick();
		
		resetButton.active = category.isDirty();
	}
	
	@Override
	public void render(MatrixStack mstack, int mouseX, int mouseY, float pticks) {
		vigiledWidgets.forEach(w -> w.visible = false);
		
		renderBackground(mstack);
		elementList.render(mstack, mouseX, mouseY, pticks);
		
		List<Widget> visibleWidgets = new LinkedList<>();
		vigiledWidgets.forEach(w -> {
			if(w.visible)
				visibleWidgets.add(w);
			w.visible = false;
		});
		
		super.render(mstack, mouseX, mouseY, pticks);
		
		MainWindow main = minecraft.getMainWindow();
		int res = (int) main.getGuiScaleFactor();
		
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(0, 40 * res, width * res, (height - 80) * res);
		for(Widget w : visibleWidgets) {
			w.visible = true;
			w.render(mstack, mouseX, mouseY, pticks);
		}
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		
		int left = 20;
		font.drawString(mstack, TextFormatting.BOLD + I18n.format("quark.gui.config.header"), left, 10, 0x48ddbc);
		font.drawString(mstack, breadcrumbs, left, 20, 0xFFFFFF);
	}

}
