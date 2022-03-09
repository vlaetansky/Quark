package vazkii.quark.content.tools.module;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.saveddata.maps.MapDecoration.Type;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.type.AbstractConfigType;

@LoadModule(category = ModuleCategory.TOOLS, hasSubscriptions = true)
public class PathfinderMapsModule extends QuarkModule {
	
	private static final Object mutex = new Object();

	public static List<TradeInfo> builtinTrades = new LinkedList<>();
	public static List<TradeInfo> customTrades = new LinkedList<>();
	public static List<TradeInfo> tradeList = new LinkedList<>();

	@Config(description = "In this section you can add custom Pathfinder Maps. This works for both vanilla and modded biomes.\n"
				+ "Each custom map must be on its own line.\n"
				+ "The format for a custom map is as follows:\n"
				+ "<id>,<level>,<min_price>,<max_price>,<color>,<name>\n\n"
				+ "With the following descriptions:\n"
				+ " - <id> being the biome's ID NAME. You can find vanilla names here - https://minecraft.gamepedia.com/Biome#Biome_IDs\n"
				+ " - <level> being the Cartographer villager level required for the map to be unlockable\n"
				+ " - <min_price> being the cheapest (in Emeralds) the map can be\n"
				+ " - <max_price> being the most expensive (in Emeralds) the map can be\n"
				+ " - <color> being a hex color (without the #) for the map to display. You can generate one here - http://htmlcolorcodes.com/\n"
				+ " - <name> being the display name of the map\n\n"
				+ "Here's an example of a map to locate Ice Mountains:\n"
				+ "minecraft:ice_mountains,2,8,14,7FE4FF,Ice Mountains Pathfinder Map")
	private List<String> customs = new LinkedList<>();
	
	@Config public static int searchRadius = 6400;
	@Config public static int searchDistanceIncrement = 8;
	@Config public static int xpFromTrade = 5;

	private static String getBiomeDescriptor(ResourceLocation rl) {
		if(rl == null)
			return "unknown";
		
		return rl.getPath();
	}

	@Override
	public void register() {
		loadTradeInfo(Biomes.SNOWY_PLAINS, true, 4, 8, 14, 0x7FE4FF);
		loadTradeInfo(Biomes.WINDSWEPT_HILLS, true, 4, 8, 14, 0x8A8A8A);
		loadTradeInfo(Biomes.DARK_FOREST, true, 4, 8, 14, 0x00590A);
		loadTradeInfo(Biomes.DESERT, true, 4, 8, 14, 0xCCB94E);
		loadTradeInfo(Biomes.SAVANNA, true, 4, 8, 14, 0x9BA562);
		loadTradeInfo(Biomes.SWAMP, true, 4, 12, 18, 0x22370F);
		loadTradeInfo(Biomes.OLD_GROWTH_PINE_TAIGA, true, 4, 12, 18, 0x5B421F);
		
		loadTradeInfo(Biomes.FLOWER_FOREST, true, 5, 12, 18, 0xDC7BEA);
		loadTradeInfo(Biomes.JUNGLE, true, 5, 16, 22, 0x22B600);
		loadTradeInfo(Biomes.BAMBOO_JUNGLE, true, 5, 16, 22, 0x3DE217);
		loadTradeInfo(Biomes.BADLANDS, true, 5, 16, 22, 0xC67F22);
		loadTradeInfo(Biomes.MUSHROOM_FIELDS, true, 5, 20, 26, 0x4D4273);
		loadTradeInfo(Biomes.ICE_SPIKES, true, 5, 20, 26, 0x41D6C9);
	}
	
	@SubscribeEvent
	public void onTradesLoaded(VillagerTradesEvent event) {
		if(event.getType() == VillagerProfession.CARTOGRAPHER)
			synchronized (mutex) {
				Int2ObjectMap<List<ItemListing>> trades = event.getTrades();
				for(TradeInfo info : tradeList)
					if(info != null)
						trades.get(info.level).add(new PathfinderMapTrade(info));
			}
	}
	
	@Override
	public void configChanged() {
		synchronized (mutex) {
			tradeList.clear();
			customTrades.clear();

			loadCustomMaps(customs);
			
			tradeList.addAll(builtinTrades);
			tradeList.addAll(customTrades);
		}
	}

	private void loadTradeInfo(ResourceKey<Biome> biome, boolean enabled, int level, int minPrice, int maxPrice, int color) {
		builtinTrades.add(new TradeInfo(biome.location(), enabled, level, minPrice, maxPrice, color));
	}
	
	private void loadCustomTradeInfo(ResourceLocation biome, boolean enabled, int level, int minPrice, int maxPrice, int color, String name) {
		customTrades.add(new TradeInfo(biome, enabled, level, minPrice, maxPrice, color, name));
	}

	private void loadCustomTradeInfo(String line) throws IllegalArgumentException {
		String[] tokens = line.split(",");
		if(tokens.length != 6)
			throw new IllegalArgumentException("Wrong number of parameters " + tokens.length + " (expected 6)");

		ResourceLocation biomeName = new ResourceLocation(tokens[0]);
		int level = Integer.parseInt(tokens[1]);
		int minPrice = Integer.parseInt(tokens[2]);
		int maxPrice = Integer.parseInt(tokens[3]);
		int color = Integer.parseInt(tokens[4], 16);
		String name = tokens[5];

		loadCustomTradeInfo(biomeName, true, level, minPrice, maxPrice, color, name);
	}

	private void loadCustomMaps(Iterable<String> lines) {
		for(String s : lines)
			try {
				loadCustomTradeInfo(s);
			} catch(IllegalArgumentException e) {
				Quark.LOG.warn("[Custom Pathfinder Maps] Error while reading custom map string \"%s\"", s);
				Quark.LOG.warn("[Custom Pathfinder Maps] - %s", e.getMessage());
			}
	}

	public static ItemStack createMap(Level world, BlockPos pos, TradeInfo info) {
		if(!(world instanceof ServerLevel))
			return ItemStack.EMPTY;

		BlockPos biomePos = MiscUtil.locateBiome((ServerLevel) world, info.biome, pos, searchRadius, searchDistanceIncrement);
		
		if(biomePos == null)
			return ItemStack.EMPTY;
			
		ItemStack stack = MapItem.create(world, biomePos.getX(), biomePos.getZ(), (byte) 2, true, true);
		// fillExplorationMap
		MapItem.renderBiomePreviewMap((ServerLevel) world, stack);
		MapItemSavedData.addTargetDecoration(stack, biomePos, "+", Type.RED_X);
		stack.setHoverName(new TranslatableComponent(info.name));

		return stack;
	}

	private static class PathfinderMapTrade implements ItemListing {

		public final TradeInfo info;

		public PathfinderMapTrade(TradeInfo info) {
			this.info = info;
		}

		@Override
		public MerchantOffer getOffer(@Nonnull Entity entity, @Nonnull Random random) {
			if(!info.enabled)
				return null;
			
			int i = random.nextInt(info.maxPrice - info.minPrice + 1) + info.minPrice;

			ItemStack itemstack = createMap(entity.level, entity.blockPosition(), info);
			if(itemstack.isEmpty())
				return null;
			
			return new MerchantOffer(new ItemStack(Items.EMERALD, i), new ItemStack(Items.COMPASS), itemstack, 12, xpFromTrade * Math.max(1, (info.level - 1)), 0.2F);
		}
	}

	public static class TradeInfo extends AbstractConfigType {

		public final ResourceLocation biome;
		public final int color;
		public final String name;

		@Config public boolean enabled;
		@Config public final int level;
		@Config public final int minPrice;
		@Config public final int maxPrice;

		TradeInfo(ResourceLocation biome, boolean enabled, int level, int minPrice, int maxPrice, int color) {
			this(biome, enabled, level, minPrice, maxPrice, color, "item.quark.biome_map." + getBiomeDescriptor(biome));
		}

		TradeInfo(ResourceLocation biome, boolean enabled, int level, int minPrice, int maxPrice, int color, String name) {
			this.biome = biome;

			this.enabled = enabled;
			this.level = level;
			this.minPrice = minPrice;
			this.maxPrice = maxPrice;
			this.color = color;
			this.name = name;
		}
		
	}

}
