package vazkii.quark.base.client.config;

import java.io.PrintStream;
import java.util.List;
import java.util.function.Supplier;

import vazkii.quark.base.client.config.gui.CheckboxButton;
import vazkii.quark.base.client.config.gui.QCategoryScreen;
import vazkii.quark.base.client.config.gui.WidgetWrapper;

public class ConfigObject<T> extends AbstractConfigElement {

	private final T defaultObj;
	private final Supplier<T> objectGetter;
	private final String displayName;
	
	private T loadedObj;
	private T currentObj;
	
	public ConfigObject(String name, String comment, T defaultObj, Supplier<T> objGetter, ConfigCategory parent) {
		super(name, comment, parent);
		this.defaultObj = defaultObj;
		this.objectGetter = objGetter;
		
		if(name.contains(" "))
			displayName = String.format("\"%s\"", name);
		else displayName = name;
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
	@SuppressWarnings("unchecked")
	public void addWidgets(QCategoryScreen parent, List<WidgetWrapper> widgets) {
		if(currentObj instanceof Boolean) {
			widgets.add(new WidgetWrapper(new CheckboxButton(230, 3, (ConfigObject<Boolean>) this)));
		} // TODO non-boolean support
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
		return !loadedObj.equals(currentObj);
	}
	
	@Override
	public void print(String pad, PrintStream out) {
		super.print(pad, out);
		
		String objStr = null;
		if(currentObj instanceof List<?>) {
			StringBuilder builder = new StringBuilder();
			builder.append("[");
			
			boolean first = true;
			for(Object obj : (List<?>) currentObj) {
				if(!first)
					builder.append(", ");
				
				builder.append("\"");
				builder.append(obj);
				builder.append("\"");
				
				first = false;
			}
			
			builder.append("]");
			objStr = builder.toString();
		} else if(currentObj instanceof String) {
			objStr = String.format("\"%s\"", currentObj);
		} else objStr = currentObj.toString();
		
		out.printf("%s%s = %s%n", pad, displayName, objStr);
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
