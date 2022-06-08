package vazkii.quark.base.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.content.building.block.VerticalSlabBlock;
import vazkii.quark.content.building.block.VerticalSlabBlock.VerticalSlabType;

import javax.annotation.Nonnull;
import java.util.Objects;

// Taken with love from PAUCAL
public class QuarkBlockStateProvider extends BlockStateProvider {
	public QuarkBlockStateProvider(DataGenerator gen, String modid,
								   ExistingFileHelper exFileHelper) {
		super(gen, modid, exFileHelper);
	}

	public ResourceLocation blockTexture(Block block, String replace, String with) {
		ResourceLocation base = blockTexture(block);
		return new ResourceLocation(base.getNamespace(), base.getPath().replace(replace, with));
	}

	public ResourceLocation blockTexture(Block block, String extension) {
		ResourceLocation base = blockTexture(block);
		return new ResourceLocation(base.getNamespace(), base.getPath() + extension);
	}

	@Override
	public ResourceLocation blockTexture(Block block) {
		if (block instanceof IQuarkBlock quarkBlock) return quarkBlock.blockTexture(this);
		return super.blockTexture(block);
	}

	public ResourceLocation baseBlockTexture(Block block) {
		return super.blockTexture(block);
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
		doorBlock(block, blockTexture(block, "_bottom"), blockTexture(block, "_top"));
	}

	public void cubeBlockAndItem(Block block, String name) {
		blockAndItem(block, models().cubeAll(name, modLoc("block/" + name)));
	}

	public void cubeBlockAndItem(Block block) {
		ModelFile file = cubeAll(block);
		simpleBlock(block, file);
		simpleBlockItem(block, file);
	}

	@Override
	public void simpleBlockItem(Block block, ModelFile model) {
		if (block.asItem() != Items.AIR)
			super.simpleBlockItem(block, model);
	}

	public void simpleBlockItem(Block block) {
		simpleBlockItem(block, models().getExistingFile(blockTexture(block)));
	}

	public void verticalSlabBlock(VerticalSlabBlock block, ResourceLocation doubleslab, ResourceLocation texture) {
		verticalSlabBlock(block, doubleslab, texture, texture, texture);
	}

	public BlockModelBuilder verticalSlab(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		return models().withExistingParent(name, modLoc("block/vertical_slab"))
				.texture("side", side)
				.texture("bottom", bottom)
				.texture("top", top);
	}

	public void tintedVerticalSlabBlock(VerticalSlabBlock block, ResourceLocation doubleslab, ResourceLocation texture) {
		tintedVerticalSlabBlock(block, doubleslab, texture, texture, texture);
	}

	public BlockModelBuilder tintedVerticalSlab(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		return models().withExistingParent(name, modLoc("block/vertical_slab_tinted"))
			.texture("side", side)
			.texture("bottom", bottom)
			.texture("top", top);
	}

	public void tintedVerticalSlabBlock(VerticalSlabBlock block, ResourceLocation doubleslab, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		verticalSlabBlock(block, tintedVerticalSlab(block.getRegistryName().getPath(), side, bottom, top), models().getExistingFile(doubleslab));
	}

	public void verticalSlabBlock(VerticalSlabBlock block, ResourceLocation doubleslab, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		verticalSlabBlock(block, verticalSlab(block.getRegistryName().getPath(), side, bottom, top), models().getExistingFile(doubleslab));
	}

	public void verticalSlabBlock(VerticalSlabBlock block, ModelFile slab, ModelFile doubleslab) {
		getVariantBuilder(block)
				.partialState().with(VerticalSlabBlock.TYPE, VerticalSlabType.NORTH).addModels(new ConfiguredModel(slab, 0, 0, true))
				.partialState().with(VerticalSlabBlock.TYPE, VerticalSlabType.SOUTH).addModels(new ConfiguredModel(slab, 0, 180, true))
				.partialState().with(VerticalSlabBlock.TYPE, VerticalSlabType.WEST).addModels(new ConfiguredModel(slab, 0, 90, true))
				.partialState().with(VerticalSlabBlock.TYPE, VerticalSlabType.EAST).addModels(new ConfiguredModel(slab, 0, 270, true))
				.partialState().with(VerticalSlabBlock.TYPE, VerticalSlabType.DOUBLE).addModels(new ConfiguredModel(doubleslab));
	}

	@Override
	protected void registerStatesAndModels() {
		for (Block block : ForgeRegistries.BLOCKS.getValues()) {
			if (block instanceof IQuarkBlock quarkBlock) {
				ResourceLocation loc = block.getRegistryName();
				if (loc != null && loc.getNamespace().equals("quark")) {
					try {
						quarkBlock.dataGen(this);
					} catch (Exception e) {
						System.err.println(e);
						System.err.println("    While generating " + quarkBlock);
					}
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
