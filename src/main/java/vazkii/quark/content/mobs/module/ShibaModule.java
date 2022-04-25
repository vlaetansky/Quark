package vazkii.quark.content.mobs.module;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements.Type;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.BiomeDictionary;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.handler.EntityAttributeHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.type.CompoundBiomeConfig;
import vazkii.quark.base.module.config.type.EntitySpawnConfig;
import vazkii.quark.base.world.EntitySpawnHandler;
import vazkii.quark.content.mobs.client.render.entity.ShibaRenderer;
import vazkii.quark.content.mobs.entity.Shiba;

@LoadModule(category = ModuleCategory.MOBS)
public class ShibaModule extends QuarkModule {

	public static EntityType<Shiba> shibaType;
	
	@Config
	public static EntitySpawnConfig spawnConfig = new EntitySpawnConfig(40, 1, 3, CompoundBiomeConfig.fromBiomeTypes(false, BiomeDictionary.Type.MOUNTAIN));

	@Config public static boolean ignoreAreasWithSkylight = false;
	
	@Override
	public void register() {
		shibaType = EntityType.Builder.of(Shiba::new, MobCategory.CREATURE)
				.sized(0.8F, 0.8F)
				.clientTrackingRange(8)
				.setCustomClientFactory((spawnEntity, world) -> new Shiba(shibaType, world))
				.build("shiba");
		RegistryHelper.register(shibaType, "shiba");
		
		EntitySpawnHandler.registerSpawn(this, shibaType, MobCategory.CREATURE, Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, spawnConfig);
		EntitySpawnHandler.addEgg(shibaType, 0xa86741, 0xe8d5b6, spawnConfig);
		
		EntityAttributeHandler.put(shibaType, Wolf::createAttributes);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		EntityRenderers.register(shibaType, ShibaRenderer::new);
	}
	
}
