package vazkii.quark.base.world;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BooleanSupplier;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.SpawnPlacements.SpawnPredicate;
import net.minecraft.world.entity.SpawnPlacements.Type;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.world.MobSpawnInfoBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import vazkii.quark.base.Quark;
import vazkii.quark.base.item.QuarkSpawnEggItem;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.type.CostSensitiveEntitySpawnConfig;
import vazkii.quark.base.module.config.type.EntitySpawnConfig;

@EventBusSubscriber(modid = Quark.MOD_ID)
public class EntitySpawnHandler {

	private static List<TrackedSpawnConfig> trackedSpawnConfigs = new LinkedList<>();

	public static <T extends Mob> void registerSpawn(QuarkModule module, EntityType<T> entityType, MobCategory classification, Type placementType, Heightmap.Types heightMapType, SpawnPredicate<T> placementPredicate, EntitySpawnConfig config) {
		SpawnPlacements.register(entityType, placementType, heightMapType, placementPredicate);

		track(module, entityType, classification, config, false);
	}
	
	public static <T extends Mob> void track(QuarkModule module, EntityType<T> entityType, MobCategory classification, EntitySpawnConfig config, boolean secondary) {
		config.setModule(module);
		trackedSpawnConfigs.add(new TrackedSpawnConfig(entityType, classification, config, secondary));
	}

	public static void addEgg(EntityType<?> entityType, int color1, int color2, EntitySpawnConfig config) {
		addEgg(entityType, color1, color2, config.module, config::isEnabled);
	}

	public static void addEgg(EntityType<?> entityType, int color1, int color2, QuarkModule module, BooleanSupplier enabledSupplier) {
		new QuarkSpawnEggItem(entityType, color1,  color2, entityType.getRegistryName().getPath() + "_spawn_egg", module, 
				new Item.Properties().tab(CreativeModeTab.TAB_MISC))
		.setCondition(enabledSupplier);
	}

	@SubscribeEvent
	public static void onBiomeLoaded(BiomeLoadingEvent ev) {
		MobSpawnInfoBuilder builder = ev.getSpawns();

		for(TrackedSpawnConfig c : trackedSpawnConfigs) {
			List<MobSpawnSettings.SpawnerData> l = builder.getSpawner(c.classification);
			if(!c.secondary)
				l.removeIf(e -> e.type.equals(c.entityType));
			
			if(c.config.isEnabled() && c.config.biomes.canSpawn(ev))
				l.add(c.entry);
				
			if(c.config instanceof CostSensitiveEntitySpawnConfig) {
				CostSensitiveEntitySpawnConfig csc = (CostSensitiveEntitySpawnConfig) c.config;
				builder.addMobCharge(c.entityType, csc.spawnCost, csc.maxCost);
			}
		}
	}

	public static void refresh() {
		for(TrackedSpawnConfig c : trackedSpawnConfigs)
			c.refresh();
	}

	private static class TrackedSpawnConfig {

		final EntityType<?> entityType;
		final MobCategory classification;
		final EntitySpawnConfig config;
		final boolean secondary;
		MobSpawnSettings.SpawnerData entry;

		TrackedSpawnConfig(EntityType<?> entityType, MobCategory classification, EntitySpawnConfig config, boolean secondary) {
			this.entityType = entityType;
			this.classification = classification;
			this.config = config;
			this.secondary = secondary;
			refresh();
		}

		void refresh() {
			entry = new MobSpawnSettings.SpawnerData(entityType, config.spawnWeight, Math.min(config.minGroupSize, config.maxGroupSize), Math.max(config.minGroupSize, config.maxGroupSize));
		}

	}

}
