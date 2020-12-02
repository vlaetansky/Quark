package vazkii.quark.base.module;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.config.ConfigResolver;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class ModuleLoader {
	
	public static final ModuleLoader INSTANCE = new ModuleLoader(); 
	
	private Map<Class<? extends QuarkModule>, QuarkModule> foundModules = new HashMap<>();
	
	private ConfigResolver config;
	
	private ModuleLoader() { }
	
	public void start() {
		findModules();
		dispatch(QuarkModule::construct);
		dispatch(QuarkModule::modulesStarted);
		resolveConfigSpec();
	}
	
	@OnlyIn(Dist.CLIENT)
	public void clientStart() {
		dispatch(QuarkModule::constructClient);
	}
	
	private void findModules() {
		ModuleFinder finder = new ModuleFinder();
		finder.findModules();
		foundModules = finder.getFoundModules();
	}
	
	private void resolveConfigSpec() {
		config = new ConfigResolver();
		config.makeSpec();
	}
	
	public void configChanged() {
		config.configChanged();
		dispatch(QuarkModule::configChanged);
	}

	@OnlyIn(Dist.CLIENT)
	public void configChangedClient() {
		dispatch(QuarkModule::configChangedClient);
	}
	
	public void setup() {
		dispatch(QuarkModule::earlySetup);
		Quark.proxy.handleQuarkConfigChange();
		dispatch(QuarkModule::setup);
	}

	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		dispatch(QuarkModule::clientSetup);
	}

	@OnlyIn(Dist.CLIENT)
	public void modelRegistry() {
		dispatch(QuarkModule::modelRegistry);
	}

	@OnlyIn(Dist.CLIENT)
	public void textureStitch(TextureStitchEvent.Pre event) {
		dispatch(m -> m.textureStitch(event));
	}

	@OnlyIn(Dist.CLIENT)
	public void postTextureStitch(TextureStitchEvent.Post event) {
		dispatch(m -> m.postTextureStitch(event));
	}
	
	public void loadComplete() {
		dispatch(QuarkModule::loadComplete);
	}
	
	private void dispatch(Consumer<QuarkModule> run) {
		foundModules.values().forEach(run);
	}
	
	public boolean isModuleEnabled(Class<? extends QuarkModule> moduleClazz) {
		QuarkModule module = getModuleInstance(moduleClazz);
		return module != null && module.enabled;
	}
	
	public QuarkModule getModuleInstance(Class<? extends QuarkModule> moduleClazz) {
		return foundModules.get(moduleClazz);
	}
	
}
