package tutellemc.civsim.gui.components;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import vg.civcraft.mc.civmodcore.inventory.gui.Clickable;

/**
 * Does nothing when clicked
 */
public class DisplayClickable extends Clickable {

    public DisplayClickable(ItemStack item) {
        super(item);
    }

    @Override
    protected void clicked(@NotNull Player clicker) {
        // Do nothing, it's a display item only
    }
}
