package vazkii.quark.content.mobs.module;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry.PlacementType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.world.EntitySpawnHandler;
import vazkii.quark.base.world.config.EntitySpawnConfig;
import vazkii.quark.base.world.config.StrictBiomeConfig;
import vazkii.quark.content.mobs.client.render.WraithRenderer;
import vazkii.quark.content.mobs.entity.WraithEntity;

@LoadModule(category = ModuleCategory.MOBS)
public class WraithModule extends QuarkModule {
	
	public static EntityType<WraithEntity> wraithType;

	@Config(description = "List of sound sets to use with wraiths.\nThree sounds must be provided per entry, separated by | (in the format idle|hurt|death). Leave blank for no sound (i.e. if a mob has no ambient noise)")
	private static List<String> wraithSounds  = Lists.newArrayList(
			"entity.sheep.ambient|entity.sheep.hurt|entity.sheep.death",
			"entity.cow.ambient|entity.cow.hurt|entity.cow.death",
			"entity.pig.ambient|entity.pig.hurt|entity.pig.death",
			"entity.chicken.ambient|entity.chicken.hurt|entity.chicken.death",
			"entity.horse.ambient|entity.horse.hurt|entity.horse.death",
			"entity.cat.ambient|entity.cat.hurt|entity.cat.death",
			"entity.wolf.ambient|entity.wolf.hurt|entity.wolf.death",
			"entity.villager.ambient|entity.villager.hurt|entity.villager.death",
			"entity.polar_bear.ambient|entity.polar_bear.hurt|entity.polar_bear.death",
			"entity.zombie.ambient|entity.zombie.hurt|entity.zombie.death",
			"entity.skeleton.ambient|entity.skeleton.hurt|entity.skeleton.death",
			"entity.spider.ambient|entity.spider.hurt|entity.spider.death",
			"|entity.creeper.hurt|entity.creeper.death",
			"entity.endermen.ambient|entity.endermen.hurt|entity.endermen.death",
			"entity.zombie_pig.ambient|entity.zombie_pig.hurt|entity.zombie_pig.death",
			"entity.witch.ambient|entity.witch.hurt|entity.witch.death",
			"entity.blaze.ambient|entity.blaze.hurt|entity.blaze.death",
			"entity.llama.ambient|entity.llama.hurt|entity.llama.death",
			"|quark:entity.stoneling.cry|quark:entity.stoneling.die",
			"quark:entity.frog.idle|quark:entity.frog.hurt|quark:entity.frog.die"
			);
	
	@Config
	public static EntitySpawnConfig spawnConfig = new EntitySpawnConfig(80, 1, 3, new StrictBiomeConfig(false, "minecraft:soul_sand_valley"));
	
	public static List<String> validWraithSounds;

	@Override
	public void construct() {
		wraithType = EntityType.Builder.create(WraithEntity::new, EntityClassification.MONSTER)
				.size(0.5F, 0.9F)
				.size(0.6F, 1.95F)
				.trackingRange(8)
				.immuneToFire()
				.setCustomClientFactory((spawnEntity, world) -> new WraithEntity(wraithType, world))
				.build("wraith");
		RegistryHelper.register(wraithType, "wraith");

		EntitySpawnHandler.registerSpawn(this, wraithType, EntityClassification.MONSTER, PlacementType.ON_GROUND, Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::canMonsterSpawnInLight, spawnConfig);
		EntitySpawnHandler.addEgg(wraithType, 0xececec, 0xbdbdbd, spawnConfig);
	}
	
	@Override
	public void setup() {
		GlobalEntityTypeAttributes.put(wraithType, WraithEntity.registerAttributes().create());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		RenderingRegistry.registerEntityRenderingHandler(wraithType, WraithRenderer::new);
	}
	
	@Override
	public void configChanged() {
		validWraithSounds = wraithSounds.stream().filter((s) -> s.split("\\|").length == 3).collect(Collectors.toList());
	}
	
}
