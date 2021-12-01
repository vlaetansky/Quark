package vazkii.quark.base.client.handler;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientRegistry;
import vazkii.quark.base.client.util.SortedKeyBinding;

@OnlyIn(Dist.CLIENT)
public class ModKeybindHandler {
	
	public static final String MISC_GROUP = "quark.gui.keygroup.misc";
	public static final String INV_GROUP = "quark.gui.keygroup.inv";
	public static final String EMOTE_GROUP = "quark.gui.keygroup.emote";
	public static final String ACCESSIBILITY_GROUP = "quark.gui.keygroup.accessibility";

	public static KeyMapping init(String s, String key, String group) {
		return init(s, key, "key.keyboard.", group, true);
	}

	public static KeyMapping init(String s, String key, String group, int sortPriority) {
		return init(s, key, "key.keyboard.", group, sortPriority, true);
	}
	
	public static KeyMapping initMouse(String s, int key, String group) {
		return init(s, Integer.toString(key), "key.mouse.", group, true);
	}

	public static KeyMapping initMouse(String s, int key, String group, int sortPriority) {
		return init(s, Integer.toString(key), "key.mouse.", group, sortPriority, true);
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
	
}
