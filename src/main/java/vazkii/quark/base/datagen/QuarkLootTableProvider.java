package vazkii.quark.base.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.AlternativeLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.loot.CanToolPerformAction;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.module.ModuleLoader;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

// Taken with love from PAUCAL
public class QuarkLootTableProvider extends LootTableProvider {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	protected final DataGenerator generator;

	public QuarkLootTableProvider(DataGenerator pGenerator) {
		super(pGenerator);
		this.generator = pGenerator;
	}

	public void dropSelfTable(Map<Block, LootTable.Builder> lootTables, Block... blocks) {
		for (var block : blocks) {
			dropSelfTable(block.getRegistryName().getPath(), block, lootTables);
		}
	}

	public void dropSelfTable(String name, Block block, Map<Block, LootTable.Builder> lootTables) {
		var pool = LootPool.lootPool()
				.name(name)
				.setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(block));
		var loot = LootTable.lootTable().withPool(pool);

		lootTables.put(block, loot);
	}

	public void makeLeafTable(Map<Block, LootTable.Builder> lootTables, Block block) {
		var leafPool = dropThisPool(block, 1)
				.when(new AlternativeLootItemCondition.Builder(
						CanToolPerformAction.canToolPerformAction(ToolActions.SHEARS_DIG),
						MatchTool.toolMatches(ItemPredicate.Builder.item()
								.hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))))
				));
		lootTables.put(block, LootTable.lootTable().withPool(leafPool));
	}

	private void makeSlabTable(Map<Block, LootTable.Builder> lootTables, Block block) {
		var leafPool = dropThisPool(block, 1)
				.apply(SetItemCountFunction.setCount(ConstantValue.exactly(2))
						.when(new LootItemBlockStatePropertyCondition.Builder(block).setProperties(
								StatePropertiesPredicate.Builder.properties().hasProperty(SlabBlock.TYPE, SlabType.DOUBLE)
						)))
				.apply(ApplyExplosionDecay.explosionDecay());
		lootTables.put(block, LootTable.lootTable().withPool(leafPool));
	}

	public LootPool.Builder dropThisPool(ItemLike item, int count) {
		return dropThisPool(item, ConstantValue.exactly(count));
	}

	public LootPool.Builder dropThisPool(ItemLike item, NumberProvider count) {
		return LootPool.lootPool()
				.setRolls(count)
				.add(LootItem.lootTableItem(item));
	}

	public void dropSelf(Map<Block, LootTable.Builder> lootTables, Block... blocks) {
		for (var block : blocks) {
			dropSelf(block, lootTables);
		}
	}

	public void dropSelf(Block block, Map<Block, LootTable.Builder> lootTables) {
		var table = LootTable.lootTable().withPool(dropThisPool(block, 1));
		lootTables.put(block, table);
	}

	public void dropThis(Block block, ItemLike drop, Map<Block, LootTable.Builder> lootTables) {
		var table = LootTable.lootTable().withPool(dropThisPool(drop, 1));
		lootTables.put(block, table);
	}

	public void dropThis(Block block, ItemLike drop, NumberProvider count,
							Map<Block, LootTable.Builder> lootTables) {
		var table = LootTable.lootTable().withPool(dropThisPool(drop, count));
		lootTables.put(block, table);
	}

	@Override
	public void run(@Nonnull HashCache cache) {
		var blockLootTables = new HashMap<Block, LootTable.Builder>();
		for (Block block : ForgeRegistries.BLOCKS.getValues()) {
			if (block instanceof IQuarkBlock quarkBlock) {
				ResourceLocation loc = block.getRegistryName();
				if (loc != null && loc.getNamespace().equals("quark")) {
					quarkBlock.dataGen(this, blockLootTables);
				}
			}
		}

		var lootTables = new HashMap<Block, LootTable.Builder>();
		ModuleLoader.INSTANCE.dataGen(this, lootTables);

		var tables = new HashMap<ResourceLocation, LootTable>();
		for (var entry : blockLootTables.entrySet()) {
			tables.put(entry.getKey().getLootTable(), entry.getValue().setParamSet(LootContextParamSets.BLOCK).build());
		}

		for (var entry : lootTables.entrySet()) {
			tables.put(entry.getKey().getLootTable(), entry.getValue().build());
		}

		var outputFolder = this.generator.getOutputFolder();
		tables.forEach((key, lootTable) -> {
			Path path = outputFolder.resolve("data/" + key.getNamespace() + "/loot_tables/" + key.getPath() + ".json");
			try {
				DataProvider.save(GSON, cache, LootTables.serialize(lootTable), path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	@Nonnull
	@Override
	public String getName() {
		return "Quark Loot Tables";
	}
}
