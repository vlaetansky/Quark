package vazkii.quark.base.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.common.extensions.IForgeItem;
import vazkii.quark.base.datagen.QuarkItemModelProvider;
import vazkii.quark.base.datagen.QuarkItemTagsProvider;
import vazkii.quark.base.module.QuarkModule;

import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;

public interface IQuarkItem extends IForgeItem {
	default Item getItem() {
		return (Item) this;
	}

	@Nullable
	QuarkModule getModule();

	default IQuarkItem setCondition(BooleanSupplier condition) {
		return this;
	}

	default boolean doesConditionApply() {
		return true;
	}

	default boolean isEnabled() {
		QuarkModule module = getModule();
		return module != null && module.enabled && doesConditionApply();
	}

	default void dataGen(QuarkItemModelProvider models) {
		models.simpleItem(getItem());
	}

	default void dataGen(QuarkItemTagsProvider tags) {
		// NO-OP
	}

}
