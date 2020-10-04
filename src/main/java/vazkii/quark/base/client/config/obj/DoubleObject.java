package vazkii.quark.base.client.config.obj;

import java.util.function.Supplier;

import vazkii.quark.base.client.config.ConfigCategory;

public class DoubleObject extends AbstractStringInputObject<Double> {

	public DoubleObject(String name, String comment, Double defaultObj, Supplier<Double> objGetter, ConfigCategory parent) {
		super(name, comment, defaultObj, objGetter, parent);
	}

}
