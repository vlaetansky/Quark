package vazkii.quark.content.tools.module;

import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
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
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.api.IRuneColorProvider;
import vazkii.quark.api.QuarkCapabilities;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
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

	@Config(description = "Set to 0 to not generate in Dungeons")
	public static int dungeonWeight = 20;

	@Config(description = "Set to 0 to not generate in Stronghold Libraries")
	public static int libraryWeight = 30;

	@Config(description = "Set to 0 to not generate in Bastions")
	public static int bastionWeight = 25;

	@Config(description = "Set to 0 to not generate in Woodland Mansions")
	public static int woodlandMansionWeight = 15;

	@Config(description = "Set to 0 to not generate in Nether Fortresses")
	public static int netherFortressWeight = 0;

	@Config(description = "Set to 0 to not generate in Underwater Ruins")
	public static int underwaterRuinWeight = 0;

	@Config(description = "Set to 0 to not generate in Monster Boxes")
	public static int monsterBoxWeight = 5;

	@Config public static int itemQuality = 2;

	@Config public static int normalUpgradeCost = 10;
	@Config public static int limitBreakUpgradeCost = 30;

	public static LootItemFunctionType tomeEnchantType;

	@Config(name = "Valid Enchantments")
	public static List<String> enchantNames = generateDefaultEnchantmentList();

	@Config
	public static boolean overleveledBooksGlowRainbow = true;

	public static Item ancient_tome;
	public static final List<Enchantment> validEnchants = new ArrayList<>();
	private static boolean initialized = false;

	@SubscribeEvent
	public void onLootTableLoad(LootTableLoadEvent event) {
		int weight = 0;
		ResourceLocation res = event.getName();
		if(res.equals(BuiltInLootTables.STRONGHOLD_LIBRARY))
			weight = libraryWeight;
		else if(res.equals(BuiltInLootTables.SIMPLE_DUNGEON))
			weight = dungeonWeight;
		else if(res.equals(BuiltInLootTables.NETHER_BRIDGE))
			weight = netherFortressWeight;
		else if(res.equals(BuiltInLootTables.WOODLAND_MANSION))
			weight = woodlandMansionWeight;
		else if(res.equals(BuiltInLootTables.UNDERWATER_RUIN_BIG) || res.equals(BuiltInLootTables.UNDERWATER_RUIN_SMALL))
			weight = underwaterRuinWeight;
		else if(res.equals(BuiltInLootTables.BASTION_TREASURE))
			weight = bastionWeight;

		else if(res.equals(MonsterBoxModule.MONSTER_BOX_LOOT_TABLE))
			weight = monsterBoxWeight;

		if(weight > 0) {
			LootPoolEntryContainer entry = LootItem.lootTableItem(ancient_tome)
					.setWeight(weight)
					.setQuality(itemQuality)
					.apply(() -> new EnchantTome(new LootItemCondition[0]))
					.build();

			MiscUtil.addToLootTable(event.getTable(), entry);
		}
	}

	@Override
	public void register() {
		ancient_tome = new AncientTomeItem(this);

		tomeEnchantType = new LootItemFunctionType(new EnchantTome.Serializer());
		Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(Quark.MOD_ID, "tome_enchant"), tomeEnchantType);

	}

	@Override
	public void setup() {
		setupEnchantList();
		initialized = true;
	}

	public static boolean isInitialized() {
		return initialized;
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onAnvilUpdate(AnvilUpdateEvent event) {
		ItemStack left = event.getLeft();
		ItemStack right = event.getRight();

		if(!left.isEmpty() && !right.isEmpty() ) {
			if(right.getItem() == ancient_tome) {
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

			else if(right.getItem() == Items.ENCHANTED_BOOK) {
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
						if (enchantment.canEnchant(left)) {
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

	@Override
	public void configChanged() {
		if(initialized)
			setupEnchantList();
	}

	private void setupEnchantList() {
		MiscUtil.initializeEnchantmentList(enchantNames, validEnchants);
		validEnchants.removeIf((ench) -> ench.getMaxLevel() == 1);
	}

	public static Enchantment getTomeEnchantment(ItemStack stack) {
		if (stack.getItem() != ancient_tome)
			return null;

		ListTag listnbt = EnchantedBookItem.getEnchantments(stack);

		for(int i = 0; i < listnbt.size(); ++i) {
			CompoundTag compoundnbt = listnbt.getCompound(i);
			Optional<Enchantment> opt = Registry.ENCHANTMENT.getOptional(ResourceLocation.tryParse(compoundnbt.getString("id")));
			if(opt.isPresent())
				return opt.orElse(null);
		}

		return null;
	}

}
