package vazkii.quark.content.tools.module;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.content.tools.client.render.PickarangRenderer;
import vazkii.quark.content.tools.entity.PickarangEntity;
import vazkii.quark.content.tools.item.PickarangItem;

@LoadModule(category = ModuleCategory.TOOLS, hasSubscriptions = true)
public class PickarangModule extends QuarkModule {
	
	public static EntityType<PickarangEntity> pickarangType;

	@Config(description = "How long it takes before the pickarang starts returning to the player if it doesn't hit anything.")
	public static int timeout = 20;
	@Config(description = "2 is Iron, 3 is Diamond.")
	public static int harvestLevel = 3;
	@Config(description = "2 is Iron, 3 is Diamond.")
	public static int netheriteHarvestLevel = 3;
	
	@Config(description = "Set to -1 to have the Pickarang be unbreakable.")
	public static int durability = 800;
	
	@Config(description = "Set to -1 to have the Flamerang be unbreakable.")
	public static int netheriteDurability = 1040;
	
	@Config(description = "22.5 is ender chests, 25.0 is monster boxes, 50 is obsidian. Most things are below 5.")
	public static double maxHardness = 20.0;
	
	@Config(description = "Set this to true to use the recipe without the Heart of Diamond, even if the Heart of Diamond is enabled.", flag = "pickarang_never_uses_heart")
	public static boolean neverUseHeartOfDiamond = false;
	@Config(description = "Set this to true to disable the short cooldown between throwing pickarangs.")
	public static boolean noCooldown = false;
	
	public static Item pickarang;
	public static Item flamerang;
	
	private static boolean isEnabled;

	@Override
	public void construct() {
		pickarangType = EntityType.Builder.<PickarangEntity>of(PickarangEntity::new, MobCategory.MISC)
				.sized(0.4F, 0.4F)
				.clientTrackingRange(4)
				.updateInterval(10) // update interval
				.setCustomClientFactory((spawnEntity, world) -> new PickarangEntity(pickarangType, world))
				.build("pickarang");
		RegistryHelper.register(pickarangType, "pickarang");

		pickarang = new PickarangItem("pickarang", this, propertiesFor(harvestLevel, durability, false), false);
		flamerang = new PickarangItem("flamerang", this, propertiesFor(netheriteHarvestLevel, netheriteDurability, true), true);
	}
	
	private static Item.Properties propertiesFor(int level, int durability, boolean netherite) {
		Item.Properties properties = new Item.Properties()
				.stacksTo(1)
				.tab(CreativeModeTab.TAB_TOOLS)
				.addToolType(ToolType.PICKAXE, harvestLevel)
				.addToolType(ToolType.AXE, harvestLevel)
				.addToolType(ToolType.SHOVEL, harvestLevel);

		if (durability > 0)
			properties.durability(durability);
		
		if(netherite)
			properties.fireResistant();
		
		return properties;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		RenderingRegistry.registerEntityRenderingHandler(pickarangType, PickarangRenderer::new);
	}
	
	@Override
	public void configChanged() {
		// Pass over to a static reference for easier computing the coremod hook
		isEnabled = this.enabled;
	}
	
    private static final ThreadLocal<PickarangEntity> ACTIVE_PICKARANG = new ThreadLocal<>();

	public static void setActivePickarang(PickarangEntity pickarang) {
		ACTIVE_PICKARANG.set(pickarang);
	}

	public static DamageSource createDamageSource(Player player) {
		PickarangEntity pickarang = ACTIVE_PICKARANG.get();

		if (pickarang == null)
			return null;

		return new IndirectEntityDamageSource("player", pickarang, player).setProjectile();
	}
	
	public static boolean getIsFireResistant(boolean vanillaVal, Entity entity) {
		if(!isEnabled || vanillaVal)
			return vanillaVal;
		
		Entity riding = entity.getVehicle();
		if(riding instanceof PickarangEntity)
			return ((PickarangEntity) riding).netherite;
		
		return false;
	}

}
