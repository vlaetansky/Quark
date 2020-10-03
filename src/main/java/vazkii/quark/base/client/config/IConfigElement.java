package vazkii.quark.base.client.config;

import java.io.PrintStream;

public interface IConfigElement extends Comparable<IConfigElement> {

	public String getName();
	public ConfigCategory getParent();
	
	public void refresh();
	public void reset(boolean hard);
	public void debug(String pad, PrintStream out);
	
}
