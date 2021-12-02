package vazkii.quark.content.mobs.module;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements.Type;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.BiomeDictionary;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.client.handler.ModelHandler;
import vazkii.quark.base.handler.BrewingHandler;
import vazkii.quark.base.handler.EntityAttributeHandler;
import vazkii.quark.base.item.QuarkItem;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.type.CompoundBiomeConfig;
import vazkii.quark.base.module.config.type.EntitySpawnConfig;
import vazkii.quark.base.recipe.FlagIngredient;
import vazkii.quark.base.world.EntitySpawnHandler;
import vazkii.quark.content.mobs.client.model.FrogModel;
import vazkii.quark.content.mobs.client.render.FrogRenderer;
import vazkii.quark.content.mobs.entity.FrogEntity;

@LoadModule(category = ModuleCategory.MOBS, hasSubscriptions = true)
public class FrogsModule extends QuarkModule {

	public static EntityType<FrogEntity> frogType;

	@Config
	public static EntitySpawnConfig spawnConfig = new EntitySpawnConfig(40, 1, 3, CompoundBiomeConfig.fromBiomeTypes(false, BiomeDictionary.Type.SWAMP));

	@Config(flag = "frog_brewing") 
	public static boolean enableBrewing = true;
	
	@Config public static boolean enableBigFunny = false;

	@OnlyIn(Dist.CLIENT)
	public static ModelLayerLocation layer;
	
	@Override
	public void construct() {
		new QuarkItem("frog_leg", this, new Item.Properties()
				.tab(CreativeModeTab.TAB_FOOD)
				.food(new FoodProperties.Builder()
						.meat()
						.nutrition(2)
						.saturationMod(0.3F)
						.build()));

		new QuarkItem("cooked_frog_leg", this, new Item.Properties()
				.tab(CreativeModeTab.TAB_FOOD)
				.food(new FoodProperties.Builder()
						.meat()
						.nutrition(4)
						.saturationMod(1.25F)
						.build()));

		Item goldenLeg = new QuarkItem("golden_frog_leg", this, new Item.Properties()
				.tab(CreativeModeTab.TAB_BREWING)
				.food(new FoodProperties.Builder()
						.meat()
						.nutrition(4)
						.saturationMod(2.5F)
						.build()))
				.setCondition(() -> enableBrewing);
		
		BrewingHandler.addPotionMix("frog_brewing",
				() -> new FlagIngredient(Ingredient.of(goldenLeg), "frogs"),
				Potions.LEAPING, Potions.LONG_LEAPING, Potions.STRONG_LEAPING);
		
		frogType = EntityType.Builder.<FrogEntity>of(FrogEntity::new, MobCategory.CREATURE)
				.sized(0.65F, 0.5F)
				.clientTrackingRange(8)
				.setCustomClientFactory((spawnEntity, world) -> new FrogEntity(frogType, world))
				.build("frog");
		RegistryHelper.register(frogType, "frog");
		
		EntitySpawnHandler.registerSpawn(this, frogType, MobCategory.CREATURE, Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, spawnConfig);
		EntitySpawnHandler.addEgg(frogType, 0xbc9869, 0xffe6ad, spawnConfig);
		
		EntityAttributeHandler.put(frogType, FrogEntity::prepareAttributes);
	}
	
	@Override
	public void clientSetup() {
		layer = ModelHandler.addModel("frog", FrogModel::createBodyLayer, FrogModel::new);
		EntityRenderers.register(frogType, FrogRenderer::new);
	}

}
