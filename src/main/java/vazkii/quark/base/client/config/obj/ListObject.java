package vazkii.quark.base.client.config.obj;

import java.util.List;
import java.util.function.Supplier;

import vazkii.quark.base.client.config.ConfigCategory;
import vazkii.quark.base.client.config.ConfigObject;
import vazkii.quark.base.client.config.gui.QCategoryScreen;
import vazkii.quark.base.client.config.gui.WidgetWrapper;
import vazkii.quark.base.client.config.gui.widget.PencilButton;

public class ListObject extends ConfigObject<List<?>> {

	public ListObject(String name, String comment, List<?> defaultObj, Supplier<List<?>> objGetter, ConfigCategory parent) {
		super(name, comment, defaultObj, objGetter, parent);
	}

	@Override
	public void addWidgets(QCategoryScreen parent, List<WidgetWrapper> widgets) {
		widgets.add(new WidgetWrapper(new PencilButton(230, 3, b -> {}))); // TODO
	}
	
	@Override
	protected String computeObjectString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		
		boolean first = true;
		for(Object obj : currentObj) {
			if(!first)
				builder.append(", ");
			
			builder.append("\"");
			builder.append(obj);
			builder.append("\"");
			
			first = false;
		}
		
		builder.append("]");
		return builder.toString();
	}

}
