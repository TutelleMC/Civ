package tutellemc.civsim.models;

import lombok.With;
import org.bukkit.inventory.ItemStack;

@With
public record ShopOffer(ItemStack input, ItemStack output, int stock) {
    boolean isAffordableBy(final ItemStack wage) {
        return input.getType().equals(wage.getType()) && wage.getAmount() >= input.getAmount();
    }
}
