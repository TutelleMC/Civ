package tutellemc.civsim.tasks;

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
    public void accept(BukkitTask bukkitTask) {
        CivSim.log().info("Running hiring task");
        nodeService.getNodes().stream().filter(Employer::canHire).forEach(employer -> {
            CivSim.log().info("Attempting to hire for %s paying %s".formatted(employer, employer.getOfferedWage()));
            final Map<Shop, List<ShopOffer>> relevantOffers =
                    shopsService.findNearbyShops(employer.getLocation(), MAX_DISTANCE_LOOK_FOR_STORES).stream()
                            .map(shop -> Map.entry(shop, shop.relevantOffers(employer.getOfferedWage())))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            final int amountOfWorkersWagesCanAfford = relevantOffers.values().stream()
                    .mapToInt(this::amountOfWorkersSustainedByOffers)
                    .sum();
            final int amountToHire = Math.min(amountOfWorkersWagesCanAfford, employer.numberOfVacantJobs());
            final int purchasesComplete = (int) relevantOffers.entrySet().stream()
                    .flatMap(v -> v.getValue().stream().map(offer -> Map.entry(v.getKey(), offer)))
                    .limit(amountToHire)
                    .filter(entry -> ItemExchangeGlue.purchaseGoods(employer, entry.getKey(), entry.getValue()))
                    .count();
            employer.hire(purchasesComplete);
            CivSim.log().info("%s has employed %s workers".formatted(employer, purchasesComplete));
        });
    }

    private int amountOfWorkersSustainedByOffers(final List<ShopOffer> relevantOffers) {
        return NEEDS.entrySet().stream()
                .mapToInt(need -> relevantOffers.stream()
                                .filter(offer -> offer.output().getType().equals(need.getKey()))
                                .mapToInt(offer -> offer.output().getAmount() * offer.stock())
                                .sum()
                        / need.getValue())
                .min()
                .orElse(0);
    }
}
