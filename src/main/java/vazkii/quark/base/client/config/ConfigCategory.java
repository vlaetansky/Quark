package vazkii.quark.base.client.config;

import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.commons.lang3.text.WordUtils;

import vazkii.quark.base.module.ModuleCategory;

public class ConfigCategory extends AbstractConfigElement {

	private List<IConfigElement> subElements = new LinkedList<>();
	
	private Map<String, ConfigObject<Boolean>> moduleOptions = new HashMap<>();
	
	private final String path;
	private final int depth;
	
	public ConfigCategory(String name, String comment, ConfigCategory parent) {
		super(name, comment, parent);
		
		if(parent == null) {
			path = name;
			depth = 0;
		} else {
			path = String.format("%s.%s", parent.path, name);
			depth = 1 + parent.depth;
		}
	}
	
	@Override
	public void refresh() {
		subElements.forEach(IConfigElement::refresh);
	}
	
	@Override
	public void reset(boolean hard) {
		subElements.forEach(e -> e.reset(hard));		
	}
	
	public ConfigCategory addCategory(String name, String comment) {
		ConfigCategory newCategory = new ConfigCategory(name, comment, this);
		subElements.add(newCategory);
		return newCategory;
	}
	
	@SuppressWarnings("unchecked")
	public <T> void addObject(String name, T default_, Supplier<T> getter, String comment, Predicate<Object> restriction) {
		ConfigObject<T> obj = new ConfigObject<T>(name, comment, default_, getter, this); 
		subElements.add(obj);
		
		if(parent == null && default_ instanceof Boolean)
			moduleOptions.put(name, (ConfigObject<Boolean>) obj);
	}
	
	@SuppressWarnings("deprecation")
	public ConfigObject<Boolean> getModuleOption(ModuleCategory category) {
		return moduleOptions.get(WordUtils.capitalizeFully(category.name));
	}
	
	public void close() {
		subElements.removeIf(e -> e instanceof ConfigCategory && ((ConfigCategory) e).subElements.isEmpty());
		Collections.sort(subElements);
	}

	@Override
	public int compareTo(IConfigElement o) {
		if(o == this)
			return 0;
		if(!(o instanceof ConfigCategory))
			return 1;
		
		return ((ConfigCategory) o).name.compareTo(name);
	}

	@Override
	public void debug(String pad, PrintStream stream) {
		stream.println();
		super.debug(pad, stream);
		stream.printf("%s[%s]%n", pad, path);
		
		final String newPad = String.format("\t%s", pad);
		subElements.forEach(e -> e.debug(newPad, stream));
	}
	
}
