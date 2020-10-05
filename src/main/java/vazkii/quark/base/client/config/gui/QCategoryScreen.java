package vazkii.quark.base.client.config.gui;

import java.util.function.Consumer;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import vazkii.quark.base.client.config.ConfigCategory;
import vazkii.quark.base.client.config.gui.widget.ConfigElementList;
import vazkii.quark.base.client.config.gui.widget.ScrollableWidgetList;

public class QCategoryScreen extends QScrollingWidgetScreen {

	public final ConfigCategory category;
	private String breadcrumbs;
	
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
	}
	
	@Override
	public void render(MatrixStack mstack, int mouseX, int mouseY, float pticks) {
		super.render(mstack, mouseX, mouseY, pticks);
		
		int left = 20;
		font.drawString(mstack, TextFormatting.BOLD + I18n.format("quark.gui.config.header"), left, 10, 0x48ddbc);
		font.drawString(mstack, breadcrumbs, left, 20, 0xFFFFFF);
	}

	@Override
	protected ScrollableWidgetList<?, ?> createWidgetList(Consumer<Widget> consumer) {
		return new ConfigElementList(this, consumer);
	}

	@Override
	protected void onClickDefault(Button b) {
		category.reset(true);
	}

	@Override
	protected void onClickDiscard(Button b) {
		category.reset(false);
	}

	@Override
	protected boolean isDirty() {
		return category.isDirty();
	}

}
