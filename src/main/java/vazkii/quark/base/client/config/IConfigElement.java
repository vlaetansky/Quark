package vazkii.quark.base.client.config;

import java.io.PrintStream;
import java.util.List;

import vazkii.quark.base.client.screen.QCategoryScreen;
import vazkii.quark.base.client.screen.WidgetWrapper;

public interface IConfigElement extends Comparable<IConfigElement> {

	public String getName();
	public String getSubtitle();
	public ConfigCategory getParent();
	
	public void refresh();
	public void reset(boolean hard);
	public void print(String pad, PrintStream out);
	
	public void addWidgets(QCategoryScreen parent, List<WidgetWrapper> widgets);
	
}
