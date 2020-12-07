package vazkii.quark.base.client.config.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import vazkii.quark.api.config.IConfigCategory;
import vazkii.quark.base.client.config.gui.widget.ConfigElementList;
import vazkii.quark.base.client.config.gui.widget.ScrollableWidgetList;

public class CategoryScreen extends AbstractScrollingWidgetScreen {

	public final IConfigCategory category;
	private String breadcrumbs;
	
	public CategoryScreen(Screen parent, IConfigCategory category) {
		super(parent);
		this.category = category;
	}
	
	@Override
	protected void init() {
		super.init();
		
		breadcrumbs = category.getName();
		IConfigCategory currCategory = category.getParent();
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
	protected ScrollableWidgetList<?, ?> createWidgetList() {
		return new ConfigElementList(this);
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
