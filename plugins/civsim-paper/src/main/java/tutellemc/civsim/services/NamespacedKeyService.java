package tutellemc.civsim.services;

import org.bukkit.NamespacedKey;
import tutellemc.civsim.CivSim;

public class NamespacedKeyService {
    public static NamespacedKey getKey(final String key) {
        return NamespacedKey.fromString(key, CivSim.getInstance());
    }
}
