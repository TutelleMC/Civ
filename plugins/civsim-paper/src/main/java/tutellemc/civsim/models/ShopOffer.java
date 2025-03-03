package tutellemc.civsim.models;

import lombok.With;
import org.bukkit.inventory.ItemStack;

@With
public record ShopOffer(ItemStack input, ItemStack output, int stock) {
    boolean isAffordableBy(final ItemStack wage) {
        return input.getType().equals(wage.getType()) && input.getAmount() <= wage.getAmount();
    }

    /**
     * Returns true if the input and output are the same, ignoring stock
     */
    boolean isSameOffer(final ShopOffer other) {
        return input.equals(other.input) && output.equals(other.output);
    }
}
