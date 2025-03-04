package tutellemc.civsim.tasks;

import static tutellemc.civsim.Utils.fromLocation;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitTask;
import tutellemc.civsim.CivSim;
import tutellemc.civsim.glue.ItemExchangeGlue;
import tutellemc.civsim.models.Employer;
import tutellemc.civsim.models.Shop;
import tutellemc.civsim.models.ShopOffer;
import tutellemc.civsim.services.NodeService;
import tutellemc.civsim.services.ShopsService;

@RequiredArgsConstructor
public class HiringTask implements Consumer<BukkitTask> {

    private static final int MAX_DISTANCE_LOOK_FOR_STORES = 2000;
    private static final Map<Material, Integer> NEEDS = Map.ofEntries(Map.entry(Material.BREAD, 10));
    private final NodeService nodeService;
    private final ShopsService shopsService;

    @Override
    public void accept(final BukkitTask bukkitTask) {
        runTask();
    }

    public void runTask() {
        CivSim.log().info("Running hiring task");
        nodeService.getNodes().stream().filter(Employer::canHire).forEach(employer -> {
            CivSim.log().info("Attempting to hire for %s paying %s".formatted(employer, employer.getOfferedWage()));
            final Map<Shop, List<ShopOffer>> relevantOffers =
                    shopsService
                            .findNearbyShops(fromLocation(employer.getLocation()), MAX_DISTANCE_LOOK_FOR_STORES)
                            .stream()
                            .map(shop -> Map.entry(shop, shop.relevantOffers(employer.getOfferedWage())))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            CivSim.log()
                    .info("Found %s relevant offers"
                            .formatted(relevantOffers.values().size()));
            final int purchasesComplete = (int) relevantOffers.entrySet().stream()
                    .flatMap(v -> v.getValue().stream().map(offer -> Map.entry(v.getKey(), offer)))
                    .limit(employer.numberOfVacantJobs())
                    .filter(entry -> ItemExchangeGlue.purchaseGoods(employer, entry.getKey(), entry.getValue()))
                    .count();
            employer.hire(purchasesComplete);
            CivSim.log().info("%s has employed %s workers".formatted(employer, purchasesComplete));
        });
    }
}
