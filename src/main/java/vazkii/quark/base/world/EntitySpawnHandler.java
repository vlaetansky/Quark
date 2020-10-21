package vazkii.quark.base.world;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BooleanSupplier;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntitySpawnPlacementRegistry.IPlacementPredicate;
import net.minecraft.entity.EntitySpawnPlacementRegistry.PlacementType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.base.item.QuarkSpawnEggItem;
import vazkii.quark.base.module.Module;
import vazkii.quark.base.world.config.EntitySpawnConfig;

public class EntitySpawnHandler {

	private static List<TrackedSpawnConfig> trackedSpawnConfigs = new LinkedList<>();
	
	public static <T extends MobEntity> void registerSpawn(Module module, EntityType<T> entityType, EntityClassification classification, PlacementType placementType, Heightmap.Type heightMapType, IPlacementPredicate<T> placementPredicate, EntitySpawnConfig config) {
		EntitySpawnPlacementRegistry.register(entityType, placementType, heightMapType, placementPredicate);
        
        config.setModule(module);
        trackedSpawnConfigs.add(new TrackedSpawnConfig(entityType, classification, config));
	}
	
	public static void addEgg(EntityType<?> entityType, int color1, int color2, EntitySpawnConfig config) {
		addEgg(entityType, color1, color2, config.module, config::isEnabled);
	}
	
	public static void addEgg(EntityType<?> entityType, int color1, int color2, Module module, BooleanSupplier enabledSupplier) {
		new QuarkSpawnEggItem(entityType, color1,  color2, entityType.getRegistryName().getPath() + "_spawn_egg", module, 
				new Item.Properties().group(ItemGroup.MISC))
				.setCondition(enabledSupplier);
	}

	
	public static void refresh() {
		for(TrackedSpawnConfig c : trackedSpawnConfigs) {
			boolean enabled = c.config.isEnabled();
			c.refresh();
			
			for(Biome b : ForgeRegistries.BIOMES.getValues()) {
				List<SpawnListEntry> l = b.getSpawns(c.classification);
				l.removeIf(e -> e.entityType == c.entityType);
				
				if(enabled && c.config.biomes.canSpawn(b))
					l.add(c.entry);
			
				if(b == Biomes.field_235252_ay_) // soul sand valley
					b.func_235059_a_(c.entityType, 0.7, 0.15); // apply entity density
			}
		}
	}
	
	private static class TrackedSpawnConfig {

		final EntityType<?> entityType;
		final EntityClassification classification;
		final EntitySpawnConfig config;
		SpawnListEntry entry;
		
		TrackedSpawnConfig(EntityType<?> entityType, EntityClassification classification, EntitySpawnConfig config) {
			this.entityType = entityType;
			this.classification = classification;
			this.config = config;
			refresh();
		}
		
		void refresh() {
			entry = new SpawnListEntry(entityType, config.spawnWeight, Math.min(config.minGroupSize, config.maxGroupSize), Math.max(config.minGroupSize, config.maxGroupSize));
		}
		
	}

}
