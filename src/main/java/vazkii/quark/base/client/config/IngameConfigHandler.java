package vazkii.quark.base.client.config;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.config.IConfigCallback;

@OnlyIn(Dist.CLIENT)
public final class IngameConfigHandler implements IConfigCallback {

	public static final IngameConfigHandler INSTANCE = new IngameConfigHandler();

	public Map<String, ConfigCategory> topLevelCategories = new LinkedHashMap<>();
	
	private ConfigCategory currCategory = null;
	
	private IngameConfigHandler() {}
	
	@Override
	public void push(String s, String comment) {
		ConfigCategory newCategory = null;
		if(currCategory == null) {
			newCategory = new ConfigCategory(s, comment, null);
			topLevelCategories.put(s, newCategory);
		} else newCategory = currCategory.addCategory(s, comment);
		
		currCategory = newCategory;
	}

	@Override
	public void pop() {
		if(currCategory != null) {
			currCategory.close();
			currCategory = currCategory.getParent();
		}
	}

	@Override
	public <T> void addEntry(String name, T default_, Supplier<T> getter, String comment, Predicate<Object> restriction) {
		if(currCategory != null)
			currCategory.addObject(name, default_, getter, comment, restriction);
	}

	public void debug() {
		if(!Quark.DEBUG_MODE)
			return;
		
		try {
			File file = new File("config", "quark-common.toml-generated");
			file.createNewFile();
			PrintStream stream = new PrintStream(file);
			
			for(String name : topLevelCategories.keySet())
				topLevelCategories.get(name).debug("", stream);
			
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
