package vazkii.quark.base.client.util;

import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author WireSegal
 * Created at 12:19 PM on 10/6/19.
 */
@OnlyIn(Dist.CLIENT)
public class SortedKeyBinding extends KeyMapping {
    private final int priority;

    public SortedKeyBinding(String description, Type type, int keyCode, String category, int priority) {
        super(description, type, keyCode, category);
        this.priority = priority;
    }

    @Override
    public int compareTo(KeyMapping keyBinding) {
        if (this.getCategory().equals(keyBinding.getCategory()) && keyBinding instanceof SortedKeyBinding)
            return Integer.compare(priority, ((SortedKeyBinding) keyBinding).priority);
        return super.compareTo(keyBinding);
    }
}
