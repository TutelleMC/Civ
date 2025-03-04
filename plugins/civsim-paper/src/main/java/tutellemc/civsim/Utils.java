package tutellemc.civsim;

import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;

public class Utils {
    public static Vec3 fromLocation(final Location location) {
        return new Vec3(location.x(), location.y(), location.z());
    }
}
