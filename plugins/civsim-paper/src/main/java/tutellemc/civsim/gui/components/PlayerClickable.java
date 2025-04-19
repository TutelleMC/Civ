package tutellemc.civsim.gui.components;

import java.util.function.Consumer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import vg.civcraft.mc.civmodcore.inventory.gui.Clickable;

/**
 * Clickable item implementation that allows behaviour to be defined as a lambda
 */
public class PlayerClickable extends Clickable {

    private final Consumer<Player> function;

    public PlayerClickable(final ItemStack item, final Consumer<Player> function) {
        super(item);
        this.function = function;
    }

    @Override
    protected void clicked(@NotNull Player clicker) {
        function.accept(clicker);
    }
}
