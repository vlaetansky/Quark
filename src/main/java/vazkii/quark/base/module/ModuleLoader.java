package vazkii.quark.base.module;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.google.common.base.Preconditions;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import vazkii.quark.base.Quark;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.item.IQuarkItem;
import vazkii.quark.base.module.config.ConfigResolver;

public final class ModuleLoader {
	
	public static final ModuleLoader INSTANCE = new ModuleLoader(); 
	
	private Map<Class<? extends QuarkModule>, QuarkModule> foundModules = new HashMap<>();
	
	private ConfigResolver config;
	private boolean clientTicked = false;
	private ParallelDispatchEvent event;
	
	private ModuleLoader() { }
	
	public void start() {
		findModules();
		dispatch("Construct", QuarkModule::construct);
		dispatch("ModulesStarted", QuarkModule::modulesStarted);
		resolveConfigSpec();
	}
	
	@OnlyIn(Dist.CLIENT)
	public void clientStart() {
		dispatch("ConstructClient", QuarkModule::constructClient);
		MinecraftForge.EVENT_BUS.register(this);
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
	
	public void register() {
		dispatch("Register", QuarkModule::register);
		config.registerConfigBoundElements();
	}
	
	public void configChanged() {
		config.configChanged();
		dispatch("ConfigChanged", QuarkModule::configChanged);
	}

	@OnlyIn(Dist.CLIENT)
	public void configChangedClient() {
		dispatch("ConfigChangedClient", QuarkModule::configChangedClient);
	}
	
	public void setup(ParallelDispatchEvent event) {
		this.event = event;
		dispatch("EarlySetup", QuarkModule::earlySetup);
		Quark.proxy.handleQuarkConfigChange();
		dispatch("Setup", QuarkModule::setup);
		event = null;
	}

	@OnlyIn(Dist.CLIENT)
	public void clientSetup(ParallelDispatchEvent event) {
		this.event = event;
		dispatch("ClientSetup", QuarkModule::clientSetup);
		event = null;
	}

	@OnlyIn(Dist.CLIENT)
	public void modelRegistry() {
		dispatch("ModelRegistry", QuarkModule::modelRegistry);
	}

	@OnlyIn(Dist.CLIENT)
	public void textureStitch(TextureStitchEvent.Pre event) {
		dispatch("TextureStitch", m -> m.textureStitch(event));
	}

	@OnlyIn(Dist.CLIENT)
	public void postTextureStitch(TextureStitchEvent.Post event) {
		dispatch("PostTextureStitch", m -> m.postTextureStitch(event));
	}
	
	public void loadComplete(ParallelDispatchEvent event) {
		this.event = event;
		dispatch("LoadComplete", QuarkModule::loadComplete);
		event = null;
	}
	
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void firstClientTick(ClientTickEvent event) {
		if(!clientTicked && event.phase == Phase.END) {
			dispatch("FirstClientTick", m -> m.firstClientTick());
			clientTicked = true;
		}
	}
	
	private void dispatch(String step, Consumer<QuarkModule> run) {
		Quark.LOG.info("Dispatching Module Event " + step);
		foundModules.values().forEach(run);
	}
	
	void enqueue(Runnable r) {
		Preconditions.checkNotNull(event);
		event.enqueueWork(r);
	}
	
	public boolean isModuleEnabled(Class<? extends QuarkModule> moduleClazz) {
		QuarkModule module = getModuleInstance(moduleClazz);
		return module != null && module.enabled;
	}
	
	public QuarkModule getModuleInstance(Class<? extends QuarkModule> moduleClazz) {
		return foundModules.get(moduleClazz);
	}
	
	public boolean isItemEnabled(Item i) {
		if(i instanceof IQuarkItem) {
			IQuarkItem qi = (IQuarkItem) i;
			if(!qi.isEnabled())
				return false;
		}
		else if(i instanceof BlockItem) {
			BlockItem bi = (BlockItem) i;
			Block b = bi.getBlock();
			if(b instanceof IQuarkBlock) {
				IQuarkBlock qb = (IQuarkBlock) b;
				if(!qb.isEnabled())
					return false;
			}
		}
		
		return true;
	}
	
}
