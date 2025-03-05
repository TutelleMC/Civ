package tutellemc.civsim.glue;

import com.untamedears.itemexchange.events.BrowseOrPurchaseEvent;
import com.untamedears.itemexchange.events.SuccessfulPurchaseEvent;
import com.untamedears.itemexchange.rules.ExchangeRule;
import com.untamedears.itemexchange.rules.ShopRule;
import com.untamedears.itemexchange.rules.TradeRule;
import java.util.ArrayList;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import tutellemc.civsim.CivSim;
import tutellemc.civsim.models.Node;
import tutellemc.civsim.models.Shop;
import tutellemc.civsim.models.ShopOffer;
import tutellemc.civsim.services.ShopsService;
import vg.civcraft.mc.civmodcore.inventory.InventoryUtils;
import vg.civcraft.mc.civmodcore.utilities.DependencyGlue;

public class ItemExchangeGlue extends DependencyGlue {

    private final ShopsService shopsService;

    private final Listener listener = new Listener() {
        @EventHandler(ignoreCancelled = true)
        public void onBrowseOrPurchaseEvent(final BrowseOrPurchaseEvent event) {
            final var location = event.getTrade().getInventory().getLocation();
            assert location != null;
            shopsService.registerShop(fromShopRule(event.getShop(), location));
        }

        @EventHandler(ignoreCancelled = true)
        public void onSuccessfulPurchase(final SuccessfulPurchaseEvent event) {
            shopsService.updateShopAt(event.getTrade().getBlock().getLocation());
        }
    };

    public ItemExchangeGlue(final CivSim plugin, final ShopsService shopsService) {
        super(plugin, "ItemExchange");
        this.shopsService = shopsService;
    }

    public static Shop fromShopRule(@NotNull final ShopRule shopRule, @NotNull final Location location) {
        if (!shopRule.isValid()) {
            return null;
        }

        final var offers = new ArrayList<>(shopRule.getTrades().stream()
                .map(ItemExchangeGlue::fromTradeRule)
                .toList());
        final var inventory = Objects.requireNonNull(shopRule.getCurrentTrade()).getInventory();
        assert inventory != null;
        return new Shop(location, offers, inventory);
    }

    @NotNull
    public static ShopOffer fromTradeRule(@NotNull final TradeRule tradeRule) {
        return new ShopOffer(
                fromExchangeRule(tradeRule.getInput()),
                fromExchangeRule(tradeRule.getOutput()),
                tradeRule.calculateStock());
    }

    /**
     * Doesnt handle modifiers but who cares
     */
    @NotNull
    public static ItemStack fromExchangeRule(final ExchangeRule exchangeRule) {
        if (exchangeRule == null || exchangeRule.getMaterial() == null) {
            return ItemStack.of(Material.AIR);
        }
        return ItemStack.of(exchangeRule.getMaterial(), exchangeRule.getAmount());
    }

    public static boolean purchaseGoods(final Node node, final Shop shop, final ShopOffer offer) {
        CivSim.log().info("%s is attempting to purchase from %s offer %s".formatted(node, shop, offer));
        final boolean purchaseSuccessful = InventoryUtils.safelyTradeBetweenInventories(
                node.getInventory(), shop.getInventory(), new ItemStack[] {offer.input()}, new ItemStack[] {
                    offer.output()
                });
        if (purchaseSuccessful) {
            shop.getOffers().remove(offer);
            shop.getOffers().add(offer.withStock(offer.stock() - 1));
        }
        return purchaseSuccessful;
    }

    @Override
    protected void onDependencyEnabled() {
        Bukkit.getPluginManager().registerEvents(this.listener, this.plugin);
    }

    @Override
    protected void onDependencyDisabled() {
        HandlerList.unregisterAll(this.listener);
    }
}
