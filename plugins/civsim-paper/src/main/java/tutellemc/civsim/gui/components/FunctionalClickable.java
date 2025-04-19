package tutellemc.civsim.gui.components;

import java.util.function.Consumer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import vg.civcraft.mc.civmodcore.inventory.gui.Clickable;

/**
 * Clickable item implementation that allows behaviour to be defined as a lambda
 * @param <P> the data payload to pass in
 */
public class FunctionalClickable<P> extends Clickable {

    private final Consumer<P> function;
    private final P data;

    public FunctionalClickable(final ItemStack item, final P data, final Consumer<P> function) {
        super(item);
        this.function = function;
        this.data = data;
    }

    @Override
    protected void clicked(@NotNull Player clicker) {
        function.accept(data);
    }
}
