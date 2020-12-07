package vazkii.quark.base.client.config;

import java.io.PrintStream;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import vazkii.quark.api.config.IConfigElement;
import vazkii.quark.base.client.config.obj.BooleanObject;
import vazkii.quark.base.client.config.obj.DoubleObject;
import vazkii.quark.base.client.config.obj.IntegerObject;
import vazkii.quark.base.client.config.obj.ListObject;
import vazkii.quark.base.client.config.obj.StringObject;

public abstract class ConfigObject<T> extends AbstractConfigElement {

	public final T defaultObj;
	public final Predicate<Object> restriction;
	protected final Supplier<T> objectGetter;
	protected final String displayName;
	
	protected T loadedObj;
	protected T currentObj;
	
	public ConfigObject(String name, String comment, T defaultObj, Supplier<T> objGetter, Predicate<Object> restriction, ConfigCategory parent) {
		super(name, comment, parent);
		this.defaultObj = defaultObj;
		this.objectGetter = objGetter;
		this.restriction = restriction;
		
		if(name.contains(" "))
			displayName = String.format("\"%s\"", name);
		else displayName = name;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> ConfigObject<?> create(String name, String comment, T defaultObj, Supplier<T> objGetter, Predicate<Object> restriction, ConfigCategory parent) {
		if(defaultObj instanceof Boolean)
			return new BooleanObject(name, comment, (Boolean) defaultObj, (Supplier<Boolean>) objGetter, restriction, parent);
		
		else if(defaultObj instanceof String)
			return new StringObject(name, comment, (String) defaultObj, (Supplier<String>) objGetter, restriction, parent);

		else if(defaultObj instanceof Integer)
			return new IntegerObject(name, comment, (Integer) defaultObj, (Supplier<Integer>) objGetter, restriction, parent);
		
		else if(defaultObj instanceof Double)
			return new DoubleObject(name, comment, (Double) defaultObj, (Supplier<Double>) objGetter, restriction, parent);
		
		else if(defaultObj instanceof List)
			return new ListObject(name, comment, (List<String>) defaultObj, (Supplier<List<String>>) objGetter, restriction, parent);
		
		else throw new IllegalArgumentException(defaultObj + " isn't a valid config object.");
	}
	
	@Override
	public String getGuiDisplayName() {
		return name;
	}

	@Override
	public void refresh() {
		currentObj = objectGetter.get();
		loadedObj = currentObj;
	}
	
	@Override
	public void clean() {
		loadedObj = currentObj;
	}
	
	@Override
	public void reset(boolean hard) {
		setCurrentObj(hard ? defaultObj : loadedObj);
	}
	
	public T getCurrentObj() {
		return currentObj;
	}
	
	public void setCurrentObj(T currentObj) {
		this.currentObj = currentObj;
		parent.updateDirty();
	}

	@Override
	public String getSubtitle() {
		String str = currentObj.toString();
		if(str.length() > 30)
			str = str.substring(0, 27) + "...";
		return str;
	}
	
	@Override
	public boolean isDirty() {
		return wouldBeDirty(currentObj);
	}
	
	public boolean wouldBeDirty(T testObject) {
		return !loadedObj.equals(testObject);
	}
	
	@Override
	public void print(String pad, PrintStream out) {
		super.print(pad, out);
		
		String objStr = computeObjectString();;
		out.printf("%s%s = %s%n", pad, displayName, objStr);
	}
	
	protected String computeObjectString() {
		return currentObj.toString();
	}

	@Override
	public int compareTo(IConfigElement o) {
		if(o == this)
			return 0;
		if(!(o instanceof ConfigObject))
			return -1;
		
		return name.compareTo(((ConfigObject<?>) o).name);
	}
	
}
