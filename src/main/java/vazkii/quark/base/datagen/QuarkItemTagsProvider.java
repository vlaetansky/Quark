package vazkii.quark.base.datagen;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.item.IQuarkItem;
import vazkii.quark.base.module.ModuleLoader;

import javax.annotation.Nonnull;

public class QuarkItemTagsProvider extends ItemTagsProvider {
	public QuarkItemTagsProvider(DataGenerator generator, BlockTagsProvider blockTagsProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
		super(generator, blockTagsProvider, modId, existingFileHelper);
	}

	private final Multimap<TagKey<Block>, TagKey<Item>> copyCache = HashMultimap.create();

	public void copyInto(@Nonnull TagKey<Block> blockTag, @Nonnull TagKey<Item> itemTag) {
		copyCache.put(blockTag, itemTag);
	}

	@Override
	protected void addTags() {
		for (Item item : ForgeRegistries.ITEMS.getValues()) {
			if (item instanceof IQuarkItem quarkItem) {
				ResourceLocation loc = item.getRegistryName();
				if (loc != null && loc.getNamespace().equals("quark")) {
					quarkItem.dataGen(this);
				}
			}
		}

		for (Block block : ForgeRegistries.BLOCKS.getValues()) {
			if (block instanceof IQuarkBlock quarkBlock) {
				ResourceLocation loc = block.getRegistryName();
				if (loc != null && loc.getNamespace().equals("quark")) {
					quarkBlock.dataGen(this);
				}
			}
		}

		ModuleLoader.INSTANCE.dataGen(this);

		for (var toCopy : copyCache.entries()) {
			copy(toCopy.getKey(), toCopy.getValue());
		}
	}

	// Widen
	@Nonnull
	@Override
	public TagAppender<Item> tag(@Nonnull TagKey<Item> itemTag) {
		return super.tag(itemTag);
	}

	@Nonnull
	@Override
	public String getName() {
		return "Quark Item Tags";
	}
}
