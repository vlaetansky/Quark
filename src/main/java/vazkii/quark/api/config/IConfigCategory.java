package vazkii.quark.api.config;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface IConfigCategory extends IConfigElement {

	public String getPath();
	public int getDepth();
	public List<IConfigElement> getSubElements();
	
	public IConfigCategory addCategory(String name, String comment);
	public <T> void addEntry(String name, T default_, Supplier<T> getter, String comment, Predicate<Object> restriction);

	public void updateDirty();
	public void close();
	
}
