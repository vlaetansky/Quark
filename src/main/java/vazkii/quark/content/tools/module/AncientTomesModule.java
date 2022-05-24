package vazkii.quark.content.tools.module;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.api.IRuneColorProvider;
import vazkii.quark.api.QuarkCapabilities;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.content.tools.item.AncientTomeItem;
import vazkii.quark.content.tools.loot.EnchantTome;
import vazkii.quark.content.world.module.MonsterBoxModule;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

@LoadModule(category = ModuleCategory.TOOLS, hasSubscriptions = true)
public class AncientTomesModule extends QuarkModule {

	private static final Object mutex = new Object();

	private static String loot(ResourceLocation lootLoc, int defaultWeight) {
		return lootLoc.toString() + "," + defaultWeight;
	}

	@Config(description = "Format is lootTable,weight. i.e. \"minecraft:chests/stronghold_library,30\"")
	public static List<String> lootTables = Lists.newArrayList(
			loot(BuiltInLootTables.STRONGHOLD_LIBRARY, 30),
			loot(BuiltInLootTables.SIMPLE_DUNGEON, 20),
			loot(BuiltInLootTables.BASTION_TREASURE, 25),
			loot(BuiltInLootTables.WOODLAND_MANSION, 15),
			loot(BuiltInLootTables.NETHER_BRIDGE, 0),
			loot(BuiltInLootTables.UNDERWATER_RUIN_BIG, 0),
			loot(BuiltInLootTables.UNDERWATER_RUIN_SMALL, 0),
			loot(MonsterBoxModule.MONSTER_BOX_LOOT_TABLE, 5)
	);

	private static final Object2IntMap<ResourceLocation> lootTableWeights = new Object2IntArrayMap<>();

	@Config public static int itemQuality = 2;

	@Config public static int normalUpgradeCost = 10;
	@Config public static int limitBreakUpgradeCost = 30;

	public static LootItemFunctionType tomeEnchantType;

	@Config(name = "Valid Enchantments")
	public static List<String> enchantNames = generateDefaultEnchantmentList();

	@Config
	public static boolean overleveledBooksGlowRainbow = true;

	@Config(description = "Master Librarians will offer to exchange Ancient Tomes, provided you give them a max-level Enchanted Book of the Tome's enchantment too.")
	public static boolean librariansExchangeAncientTomes = true;

	public static Item ancient_tome;
	public static final List<Enchantment> validEnchants = new ArrayList<>();
	private static boolean initialized = false;

	@Override
	public void register() {
		ancient_tome = new AncientTomeItem(this);

		tomeEnchantType = new LootItemFunctionType(new EnchantTome.Serializer());
		Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(Quark.MOD_ID, "tome_enchant"), tomeEnchantType);

	}

	@SubscribeEvent
	public void onTradesLoaded(VillagerTradesEvent event) {
		if(event.getType() == VillagerProfession.LIBRARIAN && librariansExchangeAncientTomes) {
			synchronized (mutex) {
				Int2ObjectMap<List<ItemListing>> trades = event.getTrades();
				trades.get(5).add(new ExchangeAncientTomesTrade());
			}
		}
	}

	@Override
	public void configChanged() {
		lootTableWeights.clear();
		for (String table : lootTables) {
			String[] split = table.split(",");
			if (split.length == 2) {
				int weight;
				ResourceLocation loc = new ResourceLocation(split[0]);
				try {
					weight = Integer.parseInt(split[1]);
				} catch (NumberFormatException e) {
					continue;
				}
				if (weight > 0)
					lootTableWeights.put(loc, weight);
			}
		}

		if(initialized)
			setupEnchantList();
	}

	@Override
	public void setup() {
		setupEnchantList();
		initialized = true;
	}

	@SubscribeEvent
	public void onLootTableLoad(LootTableLoadEvent event) {
		ResourceLocation res = event.getName();
		int weight = lootTableWeights.getOrDefault(res, 0);

		if(weight > 0) {
			LootPoolEntryContainer entry = LootItem.lootTableItem(ancient_tome)
					.setWeight(weight)
					.setQuality(itemQuality)
					.apply(() -> new EnchantTome(new LootItemCondition[0]))
					.build();

			MiscUtil.addToLootTable(event.getTable(), entry);
		}
	}

	public static boolean isInitialized() {
		return initialized;
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onAnvilUpdate(AnvilUpdateEvent event) {
		ItemStack left = event.getLeft();
		ItemStack right = event.getRight();

		if(!left.isEmpty() && !right.isEmpty() ) {
			if(right.is(ancient_tome)) {
				Enchantment ench = getTomeEnchantment(right);
				Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(left);

				if(ench != null && enchants.containsKey(ench) && enchants.get(ench) <= ench.getMaxLevel()) {
					int lvl = enchants.get(ench) + 1;
					enchants.put(ench, lvl);

					ItemStack copy = left.copy();
					EnchantmentHelper.setEnchantments(enchants, copy);

					event.setOutput(copy);
					event.setCost(lvl > ench.getMaxLevel() ? limitBreakUpgradeCost : normalUpgradeCost);
				}
			}

			else if(right.is(Items.ENCHANTED_BOOK)) {
				Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(right);
				Map<Enchantment, Integer> currentEnchants = EnchantmentHelper.getEnchantments(left);
				boolean hasOverLevel = false;
				boolean hasMatching = false;
				for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
					Enchantment enchantment = entry.getKey();
					if(enchantment == null)
						continue;

					int level = entry.getValue();
					if (level > enchantment.getMaxLevel()) {
						hasOverLevel = true;
						if (enchantment.canEnchant(left) || left.is(Items.ENCHANTED_BOOK)) {
							hasMatching = true;
							//remove incompatible enchantments
							for (Iterator<Enchantment> iterator = currentEnchants.keySet().iterator(); iterator.hasNext(); ) {
								Enchantment comparingEnchantment = iterator.next();
								if (comparingEnchantment == enchantment)
									continue;

								if (!comparingEnchantment.isCompatibleWith(enchantment)) {
									iterator.remove();
								}
							}
							currentEnchants.put(enchantment, level);
						}
					} else if (enchantment.canEnchant(left)) {
						boolean compatible = true;
						//don't apply incompatible enchantments
						for (Enchantment comparingEnchantment : currentEnchants.keySet()) {
							if (comparingEnchantment == enchantment)
								continue;

							if (comparingEnchantment != null && !comparingEnchantment.isCompatibleWith(enchantment)) {
								compatible = false;
								break;
							}
						}
						if (compatible) {
							currentEnchants.put(enchantment, level);
						}
					}
				}

				if (hasOverLevel) {
					if (hasMatching) {
						ItemStack out = left.copy();
						EnchantmentHelper.setEnchantments(currentEnchants, out);
						String name = event.getName();
						int cost = normalUpgradeCost;

						if(name != null && !name.isEmpty() && (!out.hasCustomHoverName() || !out.getHoverName().getString().equals(name))) {
							out.setHoverName(new TextComponent(name));
							cost++;
						}

						event.setOutput(out);
						event.setCost(cost);
					}
				}
			}
		}
	}

	private static boolean isOverlevel(ItemStack stack) {
		if (stack.getItem() == Items.ENCHANTED_BOOK) {
			Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
			for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
				Enchantment enchantment = entry.getKey();
				if (enchantment == null)
					continue;

				int level = entry.getValue();
				if (level > enchantment.getMaxLevel()) {
					return true;
				}
			}
		}

		return false;
	}

	private static final ResourceLocation OVERLEVEL_COLOR_HANDLER = new ResourceLocation(Quark.MOD_ID, "overlevel_rune");

	@SubscribeEvent
	public void attachRuneCapability(AttachCapabilitiesEvent<ItemStack> event) {
		if (event.getObject().getItem() == Items.ENCHANTED_BOOK) {
			IRuneColorProvider provider = new IRuneColorProvider() {
				@Override
				@OnlyIn(Dist.CLIENT)
				public int getRuneColor(ItemStack stack) {
					if (overleveledBooksGlowRainbow && isOverlevel(stack))
						return 16;
					else
						return -1;
				}
			};

			LazyOptional<IRuneColorProvider> holder = LazyOptional.of(() -> provider);

			event.addCapability(OVERLEVEL_COLOR_HANDLER, new ICapabilityProvider() {
				@Nonnull
				@Override
				public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
					return QuarkCapabilities.RUNE_COLOR.orEmpty(cap, holder);
				}
			});
		}
	}

	public static Rarity shiftRarity(ItemStack itemStack, Rarity returnValue) {
		return ModuleLoader.INSTANCE.isModuleEnabled(AncientTomesModule.class) && overleveledBooksGlowRainbow &&
				itemStack.getItem() == Items.ENCHANTED_BOOK && isOverlevel(itemStack) ? Rarity.EPIC : returnValue;

	}

	private static List<String> generateDefaultEnchantmentList() {
		Enchantment[] enchants = new Enchantment[] {
				Enchantments.FALL_PROTECTION,
				Enchantments.THORNS,
				Enchantments.SHARPNESS,
				Enchantments.SMITE,
				Enchantments.BANE_OF_ARTHROPODS,
				Enchantments.KNOCKBACK,
				Enchantments.FIRE_ASPECT,
				Enchantments.MOB_LOOTING,
				Enchantments.SWEEPING_EDGE,
				Enchantments.BLOCK_EFFICIENCY,
				Enchantments.UNBREAKING,
				Enchantments.BLOCK_FORTUNE,
				Enchantments.POWER_ARROWS,
				Enchantments.PUNCH_ARROWS,
				Enchantments.FISHING_LUCK,
				Enchantments.FISHING_SPEED,
				Enchantments.LOYALTY,
				Enchantments.RIPTIDE,
				Enchantments.IMPALING,
				Enchantments.PIERCING
		};

		List<String> strings = new ArrayList<>();
		for(Enchantment e : enchants)
			if(e != null && e.getRegistryName() != null)
				strings.add(e.getRegistryName().toString());

		return strings;
	}

	private void setupEnchantList() {
		MiscUtil.initializeEnchantmentList(enchantNames, validEnchants);
		validEnchants.removeIf((ench) -> ench.getMaxLevel() == 1);
	}

	public static Enchantment getTomeEnchantment(ItemStack stack) {
		if (stack.getItem() != ancient_tome)
			return null;

		ListTag list = EnchantedBookItem.getEnchantments(stack);

		for(int i = 0; i < list.size(); ++i) {
			CompoundTag nbt = list.getCompound(i);
			Enchantment enchant = ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.tryParse(nbt.getString("id")));
			if (enchant != null)
				return enchant;
		}

		return null;
	}

	public static boolean matchWildcardEnchantedBook(MerchantOffer offer, ItemStack comparing, ItemStack reference) {
		// Doesn't check if enabled, since this should apply to the trades that have already been generated regardless
		if (offer.getCostA().is(ancient_tome) && offer.getCostB().is(Items.ENCHANTED_BOOK) && offer.getResult().is(ancient_tome) &&
				comparing.is(Items.ENCHANTED_BOOK) && reference.is(Items.ENCHANTED_BOOK)) {
			Map<Enchantment, Integer> referenceEnchants = EnchantmentHelper.getEnchantments(reference);
			if (referenceEnchants.size() == 1) {
				Enchantment enchantment = referenceEnchants.keySet().iterator().next();
				int level = referenceEnchants.get(enchantment);

				Map<Enchantment, Integer> comparingEnchants = EnchantmentHelper.getEnchantments(comparing);
				for (var entry : comparingEnchants.entrySet()) {
					if (entry.getKey() == enchantment && entry.getValue() >= level) {
						return true;
					}
				}
			}
		}

		return false;
	}

	private class ExchangeAncientTomesTrade implements ItemListing {
		@Nullable
		@Override
		public MerchantOffer getOffer(@Nonnull Entity trader, @Nonnull Random random) {
			if (validEnchants.isEmpty() || !enabled)
				return null;
			Enchantment target = validEnchants.get(random.nextInt(validEnchants.size()));

			ItemStack anyTome = new ItemStack(ancient_tome);
			ItemStack enchantedBook = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(target, target.getMaxLevel()));
			ItemStack outputTome = AncientTomeItem.getEnchantedItemStack(target);
			return new MerchantOffer(anyTome, enchantedBook, outputTome, 3, 3, 0.2F);
		}
	}
}
