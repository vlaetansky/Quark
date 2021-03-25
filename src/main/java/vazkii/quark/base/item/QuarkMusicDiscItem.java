package vazkii.quark.base.item;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.item.Rarity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.module.QuarkModule;

public class QuarkMusicDiscItem extends MusicDiscItem implements IQuarkItem {

	private final QuarkModule module;
	private BooleanSupplier enabledSupplier = () -> true;

	public QuarkMusicDiscItem(int comparatorValue, Supplier<SoundEvent> sound, String name, QuarkModule module) {
		super(comparatorValue, sound, (new Item.Properties()).maxStackSize(1).group(ItemGroup.MISC).rarity(Rarity.RARE));

		RegistryHelper.registerItem(this, "music_disc_" + name);
		this.module = module;
	}

	@Override
	public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
		if(isEnabled() || group == ItemGroup.SEARCH)
			super.fillItemGroup(group, items);
	}

	@Override
	public QuarkMusicDiscItem setCondition(BooleanSupplier enabledSupplier) {
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
