package tutellemc.civsim.services;

import static tutellemc.civsim.Utils.fromLocation;

import com.untamedears.itemexchange.rules.ShopRule;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import tutellemc.civsim.CivSim;
import tutellemc.civsim.glue.ItemExchangeGlue;
import tutellemc.civsim.models.Shop;

/**
 * ItemExchange does not maintain an index of every shop, so we have to do it.
 * This class handles that and provides utility methods to handle the collection of stores.
 */
public class ShopsService {
    private final Map<Location, Shop> shops = new HashMap<>();

    public boolean registerShop(final Shop shop) {
        if (shop == null || shops.containsKey(shop.getLocation())) {
            return false;
        }
        return shops.put(shop.getLocation(), shop) == null;
    }

    public boolean updateShopAt(@NotNull final Location location) {
        final var shopRule = ShopRule.resolveShop(location.getBlock());
        if (shopRule == null) {
            CivSim.log().severe("Shop does not exist at location");
            return false;
        }
        return registerShop(ItemExchangeGlue.fromShopRule(shopRule, location));
    }

    public Set<Shop> findNearbyShops(final Vec3 origin, final double radius) {
        return shops.values().stream()
                .filter(shop -> origin.closerThan(fromLocation(shop.getLocation()), radius, radius))
                .collect(Collectors.toSet());
    }
}
