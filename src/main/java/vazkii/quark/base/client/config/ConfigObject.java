package vazkii.quark.base.client.config;

import java.io.PrintStream;
import java.util.List;
import java.util.function.Supplier;

public class ConfigObject<T> extends AbstractConfigElement {

	private final T defaultObj;
	private final Supplier<T> objectGetter;
	private final String displayName;
	
	public ConfigObject(String name, String comment, T defaultObj, Supplier<T> objGetter, ConfigCategory parent) {
		super(name, comment, parent);
		this.defaultObj = defaultObj;
		this.objectGetter = objGetter;
		
		if(name.contains(" "))
			displayName = String.format("\"%s\"", name);
		else displayName = name;
	}

	@Override
	public void debug(String pad, PrintStream out) {
		super.debug(pad, out);
		
		String objStr = null;
		Object curr = objectGetter.get();
		
		if(curr instanceof List<?>) {
			StringBuilder builder = new StringBuilder();
			builder.append("[");
			
			boolean first = true;
			for(Object obj : (List<?>) curr) {
				if(!first)
					builder.append(", ");
				
				builder.append("\"");
				builder.append(obj);
				builder.append("\"");
				
				first = false;
			}
			
			builder.append("]");
			objStr = builder.toString();
		} else if(curr instanceof String) {
			objStr = String.format("\"%s\"", curr);
		} else objStr = curr.toString();
		
		out.printf("%s%s = %s%n", pad, displayName, objStr);
	}

	@Override
	public int compareTo(IConfigElement o) {
		if(o == this)
			return 0;
		if(!(o instanceof ConfigObject))
			return -1;
		
		return ((ConfigObject<?>) o).name.compareTo(name);
	}
	
}
