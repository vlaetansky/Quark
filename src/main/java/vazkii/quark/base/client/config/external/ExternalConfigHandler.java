package vazkii.quark.base.client.config.external;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig.ModConfigEvent;
import vazkii.arl.util.ClientTicker;
import vazkii.quark.api.config.IConfigCategory;
import vazkii.quark.api.config.IExternalCategory;
import vazkii.quark.api.config.IQuarkConfig;
import vazkii.quark.base.client.config.ConfigCategory;
import vazkii.quark.base.client.config.IngameConfigHandler;

public final class ExternalConfigHandler implements IQuarkConfig {
	
	public static ExternalConfigHandler instance;
	
	public Multimap<String, IExternalCategory> externalCategories = HashMultimap.create();
	public ConfigCategory mockCategory = null;
	private int lastConfigChange = 0;
	
	public void setAPIHandler() {
		instance = this;
		Holder.instance = this;
		MinecraftForge.EVENT_BUS.register(this);
		
		test();
	}
	
	public void test() {
		IQuarkConfig qc = IQuarkConfig.Holder.instance;
		
		Pair<IExternalCategory, IExternalCategory> pair = qc.registerClientCommonExternalCategory("randommod", "Random Mod", qc.writeToFileCallback("randommod-common"), qc.writeToFileCallback("randommod-client"));
		
		IExternalCategory common = pair.getLeft();
		IExternalCategory client = pair.getRight();
		
		common.addEntry("Top Level Entry Test", true, () -> true);
		
		IConfigCategory general = common.addCategory("general");
		IConfigCategory visual = client.addCategory("visual");
		
		general.addEntry("Enable Potato", true, () -> true); // replace these with callbacks to your config value
		general.addEntry("Enable Thingy", false, () -> false);
		
		visual.addEntry("Potato Size", 20, () -> 20);
		visual.addEntry("Thingy Display", "Thingy", () -> "Thingy");
		
		IConfigCategory particles = visual.addCategory("particles");
		particles.addEntry("Fancy Particle", true, () -> true);
		particles.addEntry("Ultra Fancy Particle", false, () -> false);
		
		common.refresh();
		client.refresh();
	}
	
	@SubscribeEvent
	public void configChanged(ModConfigEvent event) {
		String modid = event.getConfig().getModId();
		if(externalCategories.containsKey(modid) && ClientTicker.ticksInGame - lastConfigChange > 10) { 
			lastConfigChange = ClientTicker.ticksInGame;
			for(IExternalCategory category : externalCategories.get(modid))
				category.refresh();
		}
	}
	
	public boolean hasAny() {
		return !externalCategories.isEmpty();
	}
	
	public void commit() {
		externalCategories.values().stream().forEach(IExternalCategory::commit);
	}
	
	@Override
	public IExternalCategory registerExternalCategory(String modid, String name, Consumer<IExternalCategory> onChangedCallback) {
		if(mockCategory == null)
			mockCategory = new ConfigCategory("friends", "", null);
		
		ExternalCategory category = new ExternalCategory(name, onChangedCallback, mockCategory);
		externalCategories.put(modid, category);
		mockCategory.addCategory(category);
		
		return category;
	}

	@Override
	public Consumer<IExternalCategory> writeToFileCallback(File file) {
		return c -> {
			if(c.isDirty()) {
				try {
					file.createNewFile();
					PrintStream stream = new PrintStream(file);
					c.print("", stream);
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				c.clean();
			}
		};
	}
	
}
