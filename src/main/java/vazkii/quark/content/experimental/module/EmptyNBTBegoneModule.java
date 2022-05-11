package vazkii.quark.content.experimental.module;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;

@LoadModule(name = "Empty NBT Begone", category = ModuleCategory.EXPERIMENTAL, enabledByDefault = false)
public class EmptyNBTBegoneModule extends QuarkModule {
	private static boolean staticEnabled;

	@Override
	public void configChanged() {
		staticEnabled = enabled;
	}

	public static boolean areTagsSameIgnoringEmptyTag(ItemStack stack1, ItemStack stack2) {
		if (!staticEnabled)
			return false;

		if (!stack1.isEmpty() && !stack2.isEmpty()) {
			CompoundTag tag1 = stack1.getTag();
			CompoundTag tag2 = stack2.getTag();
			return (tag1 == null && tag2 != null && tag2.isEmpty()) ||
					(tag2 == null && tag1 != null && tag1.isEmpty());
		}

		return false;
	}
}
