package vazkii.quark.base.client.config.obj;

import java.util.function.Supplier;

import vazkii.quark.base.client.config.ConfigCategory;

public class StringObject extends AbstractStringInputObject<String> {

	public StringObject(String name, String comment, String defaultObj, Supplier<String> objGetter, ConfigCategory parent) {
		super(name, comment, defaultObj, objGetter, parent);
	}

	@Override
	protected String computeObjectString() {
		return String.format("\"%s\"", currentObj);
	}
	
}
