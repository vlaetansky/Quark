package vazkii.quark.base.client.handler;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.settings.KeyModifier;
import vazkii.quark.base.client.util.PredicatedKeyBinding;
import vazkii.quark.base.client.util.SortedKeyBinding;
import vazkii.quark.base.client.util.SortedPredicatedKeyBinding;

import java.util.function.BiPredicate;

@OnlyIn(Dist.CLIENT)
public class ModKeybindHandler {

	public static final String MISC_GROUP = "quark.gui.keygroup.misc";
	public static final String INV_GROUP = "quark.gui.keygroup.inv";
	public static final String EMOTE_GROUP = "quark.gui.keygroup.emote";

	public static KeyMapping init(String s, String key, String group) {
		return init(s, key, "key.keyboard.", group, true);
	}

	public static KeyMapping init(String s, String key, String group, int sortPriority) {
		return init(s, key, "key.keyboard.", group, sortPriority, true);
	}

	public static KeyMapping init(String s, String key, String group, BiPredicate<KeyModifier, InputConstants.Key> allowed) {
		return init(s, key, "key.keyboard.", group, true);
	}

	public static KeyMapping init(String s, String key, String group, int sortPriority, BiPredicate<KeyModifier, InputConstants.Key> allowed) {
		return init(s, key, "key.keyboard.", group, sortPriority, true);
	}

	public static KeyMapping initMouse(String s, int key, String group) {
		return init(s, Integer.toString(key), "key.mouse.", group, true);
	}

	public static KeyMapping initMouse(String s, int key, String group, int sortPriority) {
		return init(s, Integer.toString(key), "key.mouse.", group, sortPriority, true);
	}

	public static KeyMapping initMouse(String s, int key, String group, BiPredicate<KeyModifier, InputConstants.Key> allowed) {
		return init(s, Integer.toString(key), "key.mouse.", group, true, allowed);
	}

	public static KeyMapping initMouse(String s, int key, String group, int sortPriority, BiPredicate<KeyModifier, InputConstants.Key> allowed) {
		return init(s, Integer.toString(key), "key.mouse.", group, sortPriority, true, allowed);
	}

	public static KeyMapping init(String s, String key, String keyType, String group, boolean prefix) {
		KeyMapping kb = new KeyMapping(prefix ? ("quark.keybind." + s) : s, (keyType.contains("mouse") ? Type.MOUSE : Type.KEYSYM),
				(key == null ? InputConstants.UNKNOWN :
						InputConstants.getKey(keyType + key)).getValue(),
				group);
		ClientRegistry.registerKeyBinding(kb);
		return kb;
	}

	public static KeyMapping init(String s, String key, String keyType, String group, int sortPriority, boolean prefix) {
		KeyMapping kb = new SortedKeyBinding(prefix ? ("quark.keybind." + s) : s, (keyType.contains("mouse") ? Type.MOUSE : Type.KEYSYM),
				(key == null ? InputConstants.UNKNOWN :
						InputConstants.getKey(keyType + key)).getValue(),
				group, sortPriority);
		ClientRegistry.registerKeyBinding(kb);
		return kb;
	}

	public static KeyMapping init(String s, String key, String keyType, String group, int sortPriority, boolean prefix, BiPredicate<KeyModifier, InputConstants.Key> allowed) {
		KeyMapping kb = new SortedPredicatedKeyBinding(prefix ? ("quark.keybind." + s) : s, (keyType.contains("mouse") ? Type.MOUSE : Type.KEYSYM),
				(key == null ? InputConstants.UNKNOWN :
						InputConstants.getKey(keyType + key)).getValue(),
				group, sortPriority, allowed);
		ClientRegistry.registerKeyBinding(kb);
		return kb;
	}

	public static KeyMapping init(String s, String key, String keyType, String group, boolean prefix, BiPredicate<KeyModifier, InputConstants.Key> allowed) {
		KeyMapping kb = new PredicatedKeyBinding(prefix ? ("quark.keybind." + s) : s, (keyType.contains("mouse") ? Type.MOUSE : Type.KEYSYM),
				(key == null ? InputConstants.UNKNOWN :
						InputConstants.getKey(keyType + key)).getValue(),
				group, allowed);
		ClientRegistry.registerKeyBinding(kb);
		return kb;
	}

}
