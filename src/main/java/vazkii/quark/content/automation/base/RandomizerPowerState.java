package vazkii.quark.content.automation.base;

import java.util.Locale;

import net.minecraft.util.StringRepresentable;

/**
 * @author WireSegal
 * Created at 10:12 AM on 8/26/19.
 */
public enum RandomizerPowerState implements StringRepresentable {
    OFF, LEFT, RIGHT;


    @Override
    public String getSerializedName() { 
        return name().toLowerCase(Locale.ROOT);
    }
}
