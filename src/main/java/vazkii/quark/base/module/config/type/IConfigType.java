package vazkii.quark.base.module.config.type;

import vazkii.quark.base.client.config.ConfigCategory;
import vazkii.quark.base.module.config.ConfigFlagManager;

public interface IConfigType {

	default void setCategory(ConfigCategory category) { }
	default void onReload(ConfigFlagManager flagManager) { }
	
}
