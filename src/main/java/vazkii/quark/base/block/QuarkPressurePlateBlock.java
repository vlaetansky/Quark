package vazkii.quark.base.block;

import net.minecraft.core.NonNullList;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.material.Material;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.datagen.QuarkBlockStateProvider;
import vazkii.quark.base.datagen.QuarkBlockTagsProvider;
import vazkii.quark.base.datagen.QuarkItemTagsProvider;
import vazkii.quark.base.module.QuarkModule;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;

/**
 * @author WireSegal
 * Created at 9:41 PM on 10/8/19.
 */
public class QuarkPressurePlateBlock extends PressurePlateBlock implements IQuarkBlock {

	private final QuarkModule module;
	private BooleanSupplier enabledSupplier = () -> true;

	public QuarkPressurePlateBlock(Sensitivity sensitivity, String regname, QuarkModule module, CreativeModeTab creativeTab, Properties properties) {
		super(sensitivity, properties);
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
	public QuarkPressurePlateBlock setCondition(BooleanSupplier enabledSupplier) {
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
		// TODO
	}

	@Override
	public void dataGen(QuarkItemTagsProvider itemTags) {
		if (material == Material.WOOD)
			itemTags.copyInto(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
	}

	@Override
	public void dataGen(QuarkBlockTagsProvider blockTags) {
		blockTags.tag(BlockTags.PRESSURE_PLATES).add(this);
		if (material == Material.WOOD)
			blockTags.tag(BlockTags.WOODEN_PRESSURE_PLATES).add(this);
		else if (material == Material.STONE)
			blockTags.tag(BlockTags.STONE_PRESSURE_PLATES).add(this);
	}
}
