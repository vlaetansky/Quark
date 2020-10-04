package vazkii.quark.base.client.config.obj;

import java.util.List;
import java.util.function.Supplier;

import vazkii.quark.base.client.config.ConfigCategory;
import vazkii.quark.base.client.config.ConfigObject;
import vazkii.quark.base.client.config.gui.QCategoryScreen;
import vazkii.quark.base.client.config.gui.WidgetWrapper;
import vazkii.quark.base.client.config.gui.widget.CheckboxButton;

public class BooleanObject extends ConfigObject<Boolean> {

	public BooleanObject(String name, String comment, Boolean defaultObj, Supplier<Boolean> objGetter, ConfigCategory parent) {
		super(name, comment, defaultObj, objGetter, parent);
	}

	@Override
	public void addWidgets(QCategoryScreen parent, List<WidgetWrapper> widgets) {
		widgets.add(new WidgetWrapper(new CheckboxButton(230, 3, this)));		
	}
	

}
