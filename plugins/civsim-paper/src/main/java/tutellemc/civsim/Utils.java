package tutellemc.civsim;

import java.util.Arrays;
import java.util.stream.Collectors;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Utils {
    public static Vec3 fromLocation(final Location location) {
        return new Vec3(location.x(), location.y(), location.z());
    }

    public static String prettyPrintInventory(@NotNull final Inventory inventory) {
        final String contents = Arrays.stream(inventory.getContents())
                .filter(item -> !(item == null || item.getType().equals(Material.AIR)))
                .map(ItemStack::toString)
                .collect(Collectors.joining(", "));
        return contents.isBlank() ? "Empty" : contents;
    }

    public static Inventory getInventoryFromBlock(@NotNull final Block block) {
        if (block.getState() instanceof Container container) {
            return container.getInventory();
        }
        return null;
    }

    public static String printCoords(@NotNull final Location location) {
        return "%s, %s, %s".formatted(location.x(), location.y(), location.z());
    }
}
