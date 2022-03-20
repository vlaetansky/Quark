package vazkii.quark.base.client.config.screen.inputtable;

import vazkii.quark.base.client.config.screen.widgets.IWidgetProvider;
import vazkii.quark.base.module.config.type.IConfigType;

public interface IInputtableConfigType<T extends IInputtableConfigType<T>> extends IWidgetProvider, IConfigType {
	
	T copy();
	void inherit(T other, boolean committing);
	void inheritDefaults(T other);
	
}
