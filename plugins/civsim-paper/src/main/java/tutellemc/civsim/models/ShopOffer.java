package tutellemc.civsim.models;

import lombok.With;
import org.bukkit.inventory.ItemStack;
import tutellemc.civsim.CivSim;

@With
public record ShopOffer(ItemStack input, ItemStack output, int stock) {
    boolean isAffordableBy(final ItemStack wage) {
        final boolean canAfford = input.getType().equals(wage.getType()) && wage.getAmount() >= input.getAmount();
        CivSim.log().info("Analysing %s when compared to wage %s, can afford? %s".formatted(toString(), wage));
        return canAfford;
    }
}
