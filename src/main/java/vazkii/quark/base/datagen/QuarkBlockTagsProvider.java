package vazkii.quark.base.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.module.ModuleLoader;

import javax.annotation.Nonnull;

public class QuarkBlockTagsProvider extends BlockTagsProvider {
	public QuarkBlockTagsProvider(DataGenerator generator, String modId, @Nullable ExistingFileHelper existingFileHelper) {
		super(generator, modId, existingFileHelper);
	}

	@Override
	protected void addTags() {
		for (Block block : ForgeRegistries.BLOCKS.getValues()) {
			if (block instanceof IQuarkBlock quarkBlock) {
				ResourceLocation loc = block.getRegistryName();
				if (loc != null && loc.getNamespace().equals("quark")) {
					quarkBlock.dataGen(this);
					TagKey<Block> mineWith = quarkBlock.mineWith();
					if (mineWith != null)
						tag(mineWith).add(block);
				}
			}
		}
		ModuleLoader.INSTANCE.dataGen(this);
	}

	// Widen
	@Nonnull
	@Override
	public TagAppender<Block> tag(@Nonnull TagKey<Block> blockTag) {
		return super.tag(blockTag);
	}

	@Nonnull
	@Override
	public String getName() {
		return "Quark Block Tags";
	}
}
