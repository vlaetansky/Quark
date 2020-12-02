package vazkii.quark.base.block;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import vazkii.arl.block.BasicBlock;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.module.QuarkModule;

import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;

public class QuarkBlock extends BasicBlock implements IQuarkBlock {
	
	private final QuarkModule module;
	private BooleanSupplier enabledSupplier = () -> true;

	public QuarkBlock(String regname, QuarkModule module, ItemGroup creativeTab, Properties properties) {
		super(regname, properties);
		this.module = module;
		
		if(creativeTab != null)
			RegistryHelper.setCreativeTab(this, creativeTab);
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if(isEnabled() || group == ItemGroup.SEARCH)
			super.fillItemGroup(group, items);
	}

	@Override
	public QuarkBlock setCondition(BooleanSupplier enabledSupplier) {
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

}
