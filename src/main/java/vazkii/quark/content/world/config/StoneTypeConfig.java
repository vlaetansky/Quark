package vazkii.quark.content.world.config;

import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.type.DimensionConfig;
import vazkii.quark.base.module.config.type.IConfigType;
import vazkii.quark.base.module.config.type.OrePocketConfig;

public class StoneTypeConfig implements IConfigType {

	@Config
	public DimensionConfig dimensions;
	@Config
	public OrePocketConfig oregen = new OrePocketConfig(0, 255, 33, 10);

	public StoneTypeConfig(DimensionConfig config) {
		dimensions = config;
	}
	
	public StoneTypeConfig() {
		this(DimensionConfig.overworld(false));
	}

}
