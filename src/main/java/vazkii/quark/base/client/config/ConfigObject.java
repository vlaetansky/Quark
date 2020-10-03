package vazkii.quark.base.client.config;

import java.io.PrintStream;
import java.util.List;

public class ConfigObject extends AbstractConfigElement {

	private final Object defaultObj;
	private final String displayName;
	
	public ConfigObject(String name, String comment, Object defaultObj, ConfigCategory parent) {
		super(name, comment, parent);
		this.defaultObj = defaultObj;
		
		if(name.contains(" "))
			displayName = String.format("\"%s\"", name);
		else displayName = name;
	}

	@Override
	public void debug(String pad, PrintStream out) {
		super.debug(pad, out);
		
		String objStr = null;
		if(defaultObj instanceof List<?>) {
			StringBuilder builder = new StringBuilder();
			builder.append("[");
			
			boolean first = true;
			for(Object obj : (List<?>) defaultObj) {
				if(!first)
					builder.append(", ");
				
				builder.append("\"");
				builder.append(obj);
				builder.append("\"");
				
				first = false;
			}
			
			builder.append("]");
			objStr = builder.toString();
		} else if(defaultObj instanceof String) {
			objStr = String.format("\"%s\"", defaultObj);
		} else objStr = defaultObj.toString();
		
		out.printf("%s%s = %s%n", pad, displayName, objStr);
	}

	@Override
	public int compareTo(IConfigElement o) {
		if(o == this)
			return 0;
		if(!(o instanceof ConfigObject))
			return -1;
		
		return ((ConfigObject) o).name.compareTo(name);
	}
	
}
