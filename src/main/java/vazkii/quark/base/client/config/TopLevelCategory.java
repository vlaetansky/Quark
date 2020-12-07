package vazkii.quark.base.client.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;

import vazkii.quark.api.config.IConfigCategory;
import vazkii.quark.base.module.ModuleCategory;

public class TopLevelCategory extends ConfigCategory {
	
	private Map<String, ConfigObject<Boolean>> moduleOptions = new HashMap<>();

	public TopLevelCategory(String name, String comment, IConfigCategory parent) {
		super(name, comment, parent);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> void addEntry(ConfigObject<T> obj, T default_) {
		super.addEntry(obj, default_);
		
		if(default_ instanceof Boolean)
			moduleOptions.put(name, (ConfigObject<Boolean>) obj);
	}
	
	@SuppressWarnings("deprecation")
	public ConfigObject<Boolean> getModuleOption(ModuleCategory category) {
		return moduleOptions.get(WordUtils.capitalizeFully(category.name));
	}

}
