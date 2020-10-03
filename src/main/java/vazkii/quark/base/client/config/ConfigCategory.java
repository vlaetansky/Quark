package vazkii.quark.base.client.config;

import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class ConfigCategory extends AbstractConfigElement {

	private List<IConfigElement> subElements = new LinkedList<>();
	
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
	
	public ConfigCategory addCategory(String name, String comment) {
		ConfigCategory newCategory = new ConfigCategory(name, comment, this);
		subElements.add(newCategory);
		return newCategory;
	}
	
	public void addObject(String name, Object default_, String comment, Predicate<Object> restriction) {
		subElements.add(new ConfigObject(name, comment, default_, this));
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
