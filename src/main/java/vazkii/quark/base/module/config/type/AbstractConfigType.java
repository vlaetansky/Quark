package vazkii.quark.base.module.config.type;

import vazkii.quark.base.client.config.ConfigCategory;

public class AbstractConfigType implements IConfigType {

	ConfigCategory category;
	
	public AbstractConfigType() { }
	
	@Override
	public void setCategory(ConfigCategory category) {
		this.category = category;
	}
	
}
