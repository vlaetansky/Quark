package vazkii.quark.base.block;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.datagen.QuarkBlockStateProvider;
import vazkii.quark.base.datagen.QuarkBlockTagsProvider;
import vazkii.quark.base.datagen.QuarkItemTagsProvider;
import vazkii.quark.base.module.QuarkModule;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;

import static net.minecraft.world.level.material.Material.WOOD;

public class QuarkFenceBlock extends FenceBlock implements IQuarkBlock {

	private final Block parent;
	private final QuarkModule module;
	private BooleanSupplier enabledSupplier = () -> true;

	public QuarkFenceBlock(String regname, Block parent, QuarkModule module, CreativeModeTab creativeTab, Properties properties) {
		super(properties);
		this.parent = parent;
		this.module = module;

		RegistryHelper.registerBlock(this, regname);
		if(creativeTab != null)
			RegistryHelper.setCreativeTab(this, creativeTab);
	}

	@Override
	public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
		if(isEnabled() || group == CreativeModeTab.TAB_SEARCH)
			super.fillItemCategory(group, items);
	}

	@Override
	public QuarkFenceBlock setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

	@Nullable
	@Override
	public QuarkModule getModule() {
		return module;
	}

	@Override
	public void dataGen(QuarkBlockStateProvider states) {
		ResourceLocation loc = states.blockTexture(parent);
		states.fenceBlock(this, loc);
		states.simpleBlockItem(this, states.itemModels().fenceInventory(getRegistryName().getPath(), loc));
	}

	@Override
	public void dataGen(QuarkItemTagsProvider itemTags) {
		itemTags.copyInto(BlockTags.FENCES, ItemTags.FENCES);
		if (material == WOOD)
			itemTags.copyInto(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
	}

	@Override
	public void dataGen(QuarkBlockTagsProvider blockTags) {
		blockTags.tag(BlockTags.FENCES).add(this);
		if (material == WOOD)
			blockTags.tag(BlockTags.WOODEN_FENCES).add(this);
	}
}
