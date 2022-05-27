package vazkii.quark.base.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.base.item.IQuarkItem;
import vazkii.quark.base.module.ModuleLoader;

import javax.annotation.Nonnull;

// Taken with love from PAUCAL
public class QuarkItemModelProvider extends ItemModelProvider {
	public QuarkItemModelProvider(DataGenerator gen, String modid,
								  ExistingFileHelper exFileHelper) {
		super(gen, modid, exFileHelper);
	}

	public void simpleItem(Item item) {
		simpleItem(item.getRegistryName());
	}

	public void simpleItem(ResourceLocation path) {
		singleTexture(path.getPath(), new ResourceLocation("item/generated"),
				"layer0", modLoc("item/" + path.getPath()));
	}

	public void brandishedItem(Item item) {
		brandishedItem(item.getRegistryName());
	}

	public void brandishedItem(ResourceLocation path) {
		singleTexture(path.getPath(), new ResourceLocation("item/handheld"),
				"layer0", modLoc("item/" + path.getPath()));
	}

	@Override
	protected void registerModels() {
		for (Item item : ForgeRegistries.ITEMS.getValues()) {
			if (item instanceof IQuarkItem quarkItem) {
				ResourceLocation loc = item.getRegistryName();
				if (loc != null && loc.getNamespace().equals("quark")) {
					quarkItem.dataGen(this);
				}
			}
		}

		ModuleLoader.INSTANCE.dataGen(this);
	}

	@Nonnull
	@Override
	public String getName() {
		return "Quark Item Models";
	}
}
