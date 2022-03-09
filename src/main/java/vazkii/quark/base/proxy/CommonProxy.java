package vazkii.quark.base.proxy;

import java.time.LocalDateTime;
import java.time.Month;

import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.arl.util.ClientTicker;
import vazkii.quark.base.Quark;
import vazkii.quark.base.capability.CapabilityHandler;
import vazkii.quark.base.handler.BrewingHandler;
import vazkii.quark.base.handler.ContributorRewardHandler;
import vazkii.quark.base.handler.FuelHandler;
import vazkii.quark.base.handler.QuarkSounds;
import vazkii.quark.base.handler.UndergroundBiomeHandler;
import vazkii.quark.base.handler.WoodSetHandler;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.config.IConfigCallback;
import vazkii.quark.base.network.QuarkNetwork;
import vazkii.quark.base.recipe.ExclusionRecipe;
import vazkii.quark.base.world.EntitySpawnHandler;
import vazkii.quark.base.world.WorldGenHandler;

public class CommonProxy {

	private int lastConfigChange = -11;
	public static boolean jingleTheBells = false;
	private boolean registerDone = false;
	
	public void start() {
		ForgeRegistries.RECIPE_SERIALIZERS.register(ExclusionRecipe.SERIALIZER);

		QuarkSounds.start();
		ModuleLoader.INSTANCE.start();
		WorldGenHandler.start();
		
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		registerListeners(bus);

		LocalDateTime now = LocalDateTime.now();
		if (now.getMonth() == Month.DECEMBER && now.getDayOfMonth() >= 16 || now.getMonth() == Month.JANUARY && now.getDayOfMonth() <= 2)
			jingleTheBells = true;
	}
	
	public void registerListeners(IEventBus bus) {
		bus.addListener(this::setup);
		bus.addListener(this::loadComplete);
		bus.addListener(this::configChanged);
		bus.addListener(this::registerCapabilities);
		bus.addGenericListener(Object.class, this::registerContent);
	}
	
	public void setup(FMLCommonSetupEvent event) {
		QuarkNetwork.setup();
		BrewingHandler.setup();
		ModuleLoader.INSTANCE.setup(event);
		initContributorRewards();

		WoodSetHandler.setup(event);
		UndergroundBiomeHandler.init(event);
	}
	
	public void loadComplete(FMLLoadCompleteEvent event) {
		ModuleLoader.INSTANCE.loadComplete(event);
		
		WorldGenHandler.loadComplete(event);
		FuelHandler.addAllWoods();
	}
	
	public void configChanged(ModConfigEvent event) {
		if(event.getConfig().getModId().equals(Quark.MOD_ID) && ClientTicker.ticksInGame - lastConfigChange > 10) { 
			lastConfigChange = ClientTicker.ticksInGame;
			handleQuarkConfigChange();
		}
	}
	
	public void registerCapabilities(RegisterCapabilitiesEvent event) {
		CapabilityHandler.registerCapabilities(event);
	}
	
	public void registerContent(RegistryEvent.Register<?> event) {
		if(registerDone)
			return;
		registerDone = true;
		
		ModuleLoader.INSTANCE.register();
		WoodSetHandler.register();
	}
	
	public void handleQuarkConfigChange() {
		ModuleLoader.INSTANCE.configChanged();
		EntitySpawnHandler.refresh();
	}
	
	protected void initContributorRewards() {
		ContributorRewardHandler.init();
	}
	
	public IConfigCallback getConfigCallback() {
		return new IConfigCallback.Dummy();
	}

}
