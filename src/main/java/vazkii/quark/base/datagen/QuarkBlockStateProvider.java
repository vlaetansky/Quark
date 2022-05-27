package vazkii.quark.base.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.module.ModuleLoader;

import javax.annotation.Nonnull;
import java.util.Objects;

// Taken with love from PAUCAL
public class QuarkBlockStateProvider extends BlockStateProvider {
	public QuarkBlockStateProvider(DataGenerator gen, String modid,
								   ExistingFileHelper exFileHelper) {
		super(gen, modid, exFileHelper);
	}

	public void simpleItem(Block block) {
		simpleItem(block.getRegistryName());
	}

	public void simpleItem(Block block, ResourceLocation path) {
		itemModels().singleTexture(block.getRegistryName().getPath(), new ResourceLocation("item/generated"),
				"layer0", path);
	}

	public void simpleItem(ResourceLocation path) {
		itemModels().singleTexture(path.getPath(), new ResourceLocation("item/generated"),
				"layer0", modLoc("block/" + path.getPath()));
	}

	private static String registryPath(Block block) {
		ResourceLocation loc = block.getRegistryName();
		return Objects.toString(loc == null ? null : loc.getPath());
	}

	public void blockAndItem(Block block, BlockModelBuilder model) {
		simpleBlock(block, model);
		simpleBlockItem(block, model);
	}

	public void doorBlock(DoorBlock block) {
		doorBlock(block, modLoc("block/" + block + "_lower"), modLoc("block/" + block + "_lower"));
	}

	public void cubeBlockAndItem(Block block, String name) {
		blockAndItem(block, models().cubeAll(name, modLoc("block/" + name)));
	}

	public void cubeBlockAndItem(Block block) {
		simpleBlockItem(block, cubeAll(block));
	}

	@Override
	public void simpleBlockItem(Block block, ModelFile model) {
		if (block.asItem() != Items.AIR)
			super.simpleBlockItem(block, model);
	}

	public void simpleBlockItem(Block block) {
		simpleBlockItem(block, models().getExistingFile(modLoc("block/" + block.getRegistryName().getPath())));
	}

	@Override
	protected void registerStatesAndModels() {
		for (Block block : ForgeRegistries.BLOCKS.getValues()) {
			if (block instanceof IQuarkBlock quarkBlock) {
				ResourceLocation loc = block.getRegistryName();
				if (loc != null && loc.getNamespace().equals("quark")) {
					quarkBlock.dataGen(this);
				}
			}
		}

		ModuleLoader.INSTANCE.dataGen(this);
	}

	@Nonnull
	@Override
	public String getName() {
		return "Quark Block Models";
	}
}
