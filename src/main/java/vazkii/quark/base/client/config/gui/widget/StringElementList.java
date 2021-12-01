package vazkii.quark.base.client.config.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import vazkii.quark.base.client.config.gui.ListInputScreen;
import vazkii.quark.base.client.config.gui.WidgetWrapper;

public class StringElementList extends ScrollableWidgetList<ListInputScreen, StringElementList.Entry>{

	public StringElementList(ListInputScreen parent) {
		super(parent);
	}
	
	@Override
	protected void findEntries() {
		int i = 0;
		for(String s : parent.list) {
			addEntry(new Entry(parent, s, i));
			i++;
		}
		
		addEntry(new Entry(parent, null, 0));
	}

	public static final class Entry extends ScrollableWidgetList.Entry<Entry> {
		
		public final String initialString;
		
		public String string;
		
		public Entry(ListInputScreen parent, String s, int index) {
			initialString = string = s;
			
			if(s != null) {
				Minecraft mc = Minecraft.getInstance();
				EditBox field = new EditBox(mc.font, 10, 3, 210, 20, new TextComponent(""));
				field.setMaxLength(256);
				field.setValue(initialString);
				field.moveCursorTo(0);
				field.setResponder(str -> parent.list.set(index, str));
				children.add(new WidgetWrapper(field));
				
				children.add(new WidgetWrapper(new Button(230, 3, 20, 20, new TextComponent("-").withStyle(ChatFormatting.RED), b -> parent.remove(index)))); 
			} else {
				children.add(new WidgetWrapper(new Button(10, 3, 20, 20, new TextComponent("+").withStyle(ChatFormatting.GREEN), b -> parent.addNew())));
			}
		}
		
		@Override
		public void render(PoseStack mstack, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered, float pticks) {
			super.render(mstack, index, rowTop, rowLeft, rowWidth, rowHeight, mouseX, mouseY, hovered, pticks);
			
			if(initialString != null)
				drawBackground(mstack, index, rowTop, rowLeft, rowWidth, rowHeight, mouseX, mouseY, hovered);
		}
		
	}

	
}
