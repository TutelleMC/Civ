package tutellemc.civsim.models;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter
@RequiredArgsConstructor
public class Shop implements Node {

    private final Location location;
    private final List<ShopOffer> offers;
    private final Inventory inventory;

    public List<ShopOffer> relevantOffers(final ItemStack wage) {
        return offers.stream().filter(offer -> offer.isAffordableBy(wage)).toList();
    }
}
