package vazkii.quark.base.block;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraftforge.common.Tags;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.datagen.QuarkBlockStateProvider;
import vazkii.quark.base.datagen.QuarkBlockTagsProvider;
import vazkii.quark.base.datagen.QuarkItemTagsProvider;
import vazkii.quark.base.module.QuarkModule;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;

import static net.minecraft.world.level.material.Material.WOOD;

/**
 * @author WireSegal
 * Created at 9:14 PM on 10/8/19.
 */
public class QuarkFenceGateBlock extends FenceGateBlock implements IQuarkBlock {

	private final Block parent;
	private final QuarkModule module;
	private BooleanSupplier enabledSupplier = () -> true;

	public QuarkFenceGateBlock(String regname, Block parent, QuarkModule module, CreativeModeTab creativeTab, Properties properties) {
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
	public QuarkFenceGateBlock setCondition(BooleanSupplier enabledSupplier) {
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
		states.fenceGateBlock(this, loc);
		states.simpleBlockItem(this, states.itemModels().fenceGate(getRegistryName().getPath(), loc));
	}

	@Override
	public void dataGen(QuarkItemTagsProvider itemTags) {
		itemTags.copyInto(BlockTags.FENCE_GATES, Tags.Items.FENCE_GATES);
		if (material == WOOD)
			itemTags.copyInto(Tags.Blocks.FENCE_GATES_WOODEN, Tags.Items.FENCE_GATES_WOODEN);
	}

	@Override
	public void dataGen(QuarkBlockTagsProvider blockTags) {
		blockTags.tag(BlockTags.FENCE_GATES).add(this);
		if (material == WOOD)
			blockTags.tag(Tags.Blocks.FENCE_GATES_WOODEN).add(this);
	}

}
