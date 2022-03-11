package vazkii.quark.base.item;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.Block;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.module.QuarkModule;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.BooleanSupplier;

public class QuarkSignItem extends SignItem implements IQuarkItem {

	private final QuarkModule module;
	private BooleanSupplier enabledSupplier = () -> true;

	public QuarkSignItem(QuarkModule module, Block sign, Block wallSign) {
		super(new Item.Properties().stacksTo(16).tab(CreativeModeTab.TAB_DECORATIONS), sign, wallSign);

		RegistryHelper.registerItem(this, Objects.toString(sign.getRegistryName()));
		this.module = module;
	}

	@Override
	public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
		if(isEnabled() || group == CreativeModeTab.TAB_SEARCH)
			super.fillItemCategory(group, items);
	}

	@Override
	public QuarkSignItem setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public QuarkModule getModule() {
		return module;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

}
