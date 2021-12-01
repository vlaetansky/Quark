package vazkii.quark.content.automation.base;

import java.util.Locale;

import net.minecraft.util.IStringSerializable;

/**
 * @author WireSegal
 * Created at 10:12 AM on 8/26/19.
 */
public enum RandomizerPowerState implements IStringSerializable {
    OFF, LEFT, RIGHT;


    @Override
    public String getString() { 
        return name().toLowerCase(Locale.ROOT);
    }
}
