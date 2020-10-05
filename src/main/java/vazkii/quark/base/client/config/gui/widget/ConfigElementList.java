package vazkii.quark.base.client.config.gui.widget;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import vazkii.quark.base.client.TopLayerTooltipHandler;
import vazkii.quark.base.client.config.ConfigCategory;
import vazkii.quark.base.client.config.ConfigObject;
import vazkii.quark.base.client.config.IConfigElement;
import vazkii.quark.base.client.config.gui.CategoryScreen;

public class ConfigElementList extends ScrollableWidgetList<CategoryScreen, ConfigElementList.Entry> {

	public ConfigElementList(CategoryScreen parent, Consumer<Widget> widgetConsumer) {
		super(parent, widgetConsumer);
	}

	@Override
	protected void findEntries() {
		boolean isObject = true;
		for(IConfigElement elm : parent.category.subElements) {
			boolean wasObject = isObject;
			isObject = elm instanceof ConfigObject;
			
			if(wasObject && !isObject)
				addEntry(new Entry(parent, null)); // separator
			
			Entry entry = new Entry(parent, elm); 
			addEntry(entry);
		}		
	}

	@Override
	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 20;
	}

	@Override
	public int getRowWidth() {
		return super.getRowWidth() + 50;
	}

	@Override
	protected boolean isFocused() {
		return false;
	}

	public static final class Entry extends ScrollableWidgetList.Entry<Entry> {

		private final IConfigElement element;

		public Entry(CategoryScreen parent, IConfigElement element) {
			this.element = element;
			
			if(element != null)
				element.addWidgets(parent, children);
		}
		
		@Override
		public void render(MatrixStack mstack, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered, float pticks) {
			super.render(mstack, index, rowTop, rowLeft, rowWidth, rowHeight, mouseX, mouseY, hovered, pticks);
			
			Minecraft mc = Minecraft.getInstance();
			
			if(element != null) {
				int effIndex = index + 1;
				if(element instanceof ConfigCategory)
					effIndex--; // compensate for the divider
				
				if(effIndex % 2 == 0)
					fill(mstack, rowLeft, rowTop, rowLeft + rowWidth, rowTop + rowHeight, 0x66000000);

				int left = rowLeft + 10;
				int top = rowTop + 4;
				
				String name = element.getGuiDisplayName();
				if(element.isDirty())
					name += TextFormatting.GOLD + "*";
				
				int len = mc.fontRenderer.getStringWidth(name);
				int maxLen = rowWidth - 85;
				String originalName = null;
				if(len > maxLen) {
					originalName = name;
					do {
						name = name.substring(0, name.length() - 1);
						len = mc.fontRenderer.getStringWidth(name);
					} while(len > maxLen);
					
					name += "...";
				}
				
				List<String> tooltip = element.getTooltip();
				if(originalName != null) {
					if(tooltip == null) {
						tooltip = new LinkedList<>();
						tooltip.add(originalName);
					} else {
						tooltip.add(0, "");
						tooltip.add(0, originalName);
					}
				}
				
				if(tooltip != null) {
					int hoverLeft = left + mc.fontRenderer.getStringWidth(name + " ");
					int hoverRight = hoverLeft + mc.fontRenderer.getStringWidth("(?)");
					
					name += (TextFormatting.AQUA + " (?)");
					if(mouseX >= hoverLeft && mouseX < hoverRight && mouseY >= top && mouseY < (top + 10))
						TopLayerTooltipHandler.setTooltip(tooltip, mouseX, mouseY);
				}
				
				mc.fontRenderer.drawStringWithShadow(mstack, name, left, top, 0xFFFFFF);
				mc.fontRenderer.drawStringWithShadow(mstack, element.getSubtitle(), left, top + 10, 0x999999);
			} else {
				String s = I18n.format("quark.gui.config.subcategories");
				mc.fontRenderer.drawStringWithShadow(mstack, s, rowLeft + (rowWidth - mc.fontRenderer.getStringWidth(s)) / 2, rowTop + 7, 0x6666FF);
			}
		}

	}

}
