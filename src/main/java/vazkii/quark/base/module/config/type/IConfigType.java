package vazkii.quark.base.module.config.type;

import vazkii.quark.base.module.config.ConfigFlagManager;

public interface IConfigType {

	default void onReload(ConfigFlagManager flagManager) { }
	
}
