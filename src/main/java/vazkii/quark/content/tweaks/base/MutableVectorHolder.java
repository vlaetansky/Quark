package vazkii.quark.content.tweaks.base;

import net.minecraft.util.math.vector.Vector3d;

/**
 * @author WireSegal
 * Created at 11:48 AM on 9/2/19.
 */
public class MutableVectorHolder {
    public double x, y, z;

    public void importFrom(Vector3d vec) {
        x = vec.x;
        y = vec.y;
        z = vec.z;
    }
}
