package vazkii.quark.base.item;

import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.NonNullList;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.module.QuarkModule;

import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;

public class QuarkSpawnEggItem extends SpawnEggItem {

	private final QuarkModule module;
	private BooleanSupplier enabledSupplier = () -> true;

	public QuarkSpawnEggItem(EntityType<?> type, int primaryColor, int secondaryColor, String regname, QuarkModule module, Properties properties) {
		super(type, primaryColor, secondaryColor, properties);

		RegistryHelper.registerItem(this, regname);
		this.module = module;
	}

	@Override
	public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
		if(isEnabled() || group == ItemGroup.SEARCH)
			super.fillItemGroup(group, items);
	}

	public QuarkSpawnEggItem setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}


	public boolean isEnabled() {
		return module != null && module.enabled && enabledSupplier.getAsBoolean();
	}

}
