package vazkii.quark.content.mobs.module;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements.Type;
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
import vazkii.quark.base.module.config.type.DimensionConfig;
import vazkii.quark.base.module.config.type.EntitySpawnConfig;
import vazkii.quark.base.world.EntitySpawnHandler;
import vazkii.quark.content.mobs.client.render.entity.ToretoiseRenderer;
import vazkii.quark.content.mobs.entity.Toretoise;

@LoadModule(category = ModuleCategory.MOBS, hasSubscriptions = true)
public class ToretoiseModule extends QuarkModule {

	public static EntityType<Toretoise> toretoiseType;
	
	@Config public static int maxYLevel = 32;
	
	@Config(description="The number of ticks from mining a tortoise until feeding it could cause it to regrow.")
	public static int cooldownTicks = 20 * 60;
	
	@Config(description="The items that can be fed to toretoises to make them regrow ores.")
	public static List<String> foods = Lists.newArrayList("minecraft:glow_berries"); 
	
	@Config(description="Feeding a toretoise after cooldown will regrow them with a one-in-this-number chance. "
			+ "Set to 1 to always regrow, or 0 to disable.")
	public static int regrowChance = 3;
	
	@Config
	public static DimensionConfig dimensions = DimensionConfig.overworld(false);
	
	@Config 
	public static EntitySpawnConfig spawnConfig = new EntitySpawnConfig(100, 1, 1, CompoundBiomeConfig.fromBiomeTypes(true, BiomeDictionary.Type.VOID, BiomeDictionary.Type.NETHER, BiomeDictionary.Type.END));
	
	@Override
	public void construct() {
		toretoiseType = EntityType.Builder.<Toretoise>of(Toretoise::new, MobCategory.CREATURE)
				.sized(2F, 1F)
				.clientTrackingRange(8)
				.fireImmune()
				.setCustomClientFactory((spawnEntity, world) -> new Toretoise(toretoiseType, world))
				.build("toretoise");

		RegistryHelper.register(toretoiseType, "toretoise");
		
		EntitySpawnHandler.registerSpawn(this, toretoiseType, MobCategory.MONSTER, Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Toretoise::spawnPredicate, spawnConfig);
		EntitySpawnHandler.addEgg(toretoiseType, 0x55413b, 0x383237, spawnConfig);
		
		EntityAttributeHandler.put(toretoiseType, Toretoise::prepareAttributes);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		EntityRenderers.register(toretoiseType, ToretoiseRenderer::new);
	}
	
}
