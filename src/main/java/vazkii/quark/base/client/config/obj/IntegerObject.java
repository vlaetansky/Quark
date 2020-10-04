package vazkii.quark.base.client.config.obj;

import java.util.function.Supplier;

import vazkii.quark.base.client.config.ConfigCategory;

public class IntegerObject extends AbstractStringInputObject<Integer> {

	public IntegerObject(String name, String comment, Integer defaultObj, Supplier<Integer> objGetter, ConfigCategory parent) {
		super(name, comment, defaultObj, objGetter, parent);
	}

}
