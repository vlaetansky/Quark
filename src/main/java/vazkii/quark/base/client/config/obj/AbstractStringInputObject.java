package vazkii.quark.base.client.config.obj;

import java.util.List;
import java.util.function.Supplier;

import vazkii.quark.base.client.config.ConfigCategory;
import vazkii.quark.base.client.config.ConfigObject;
import vazkii.quark.base.client.config.gui.PencilButton;
import vazkii.quark.base.client.config.gui.QCategoryScreen;
import vazkii.quark.base.client.config.gui.WidgetWrapper;

public class AbstractStringInputObject<T> extends ConfigObject<T> {

	public AbstractStringInputObject(String name, String comment, T defaultObj, Supplier<T> objGetter, ConfigCategory parent) {
		super(name, comment, defaultObj, objGetter, parent);
	}

	@Override
	public void addWidgets(QCategoryScreen parent, List<WidgetWrapper> widgets) {
		widgets.add(new WidgetWrapper(new PencilButton(230, 3, b -> {}))); // TODO
	}

}
