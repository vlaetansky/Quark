package vazkii.quark.base.item;

import vazkii.quark.base.module.QuarkModule;

import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;

public interface IQuarkItem {

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

}
