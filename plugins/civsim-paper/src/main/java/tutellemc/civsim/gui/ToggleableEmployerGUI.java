package tutellemc.civsim.gui;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import tutellemc.civsim.gui.components.FunctionalClickable;
import tutellemc.civsim.models.Employer;
import tutellemc.civsim.models.Toggleable;
import vg.civcraft.mc.civmodcore.inventory.gui.ClickableInventory;

@RequiredArgsConstructor
public class ToggleableEmployerGUI<T extends Employer & Toggleable> implements GUI{
    private final String guiName;
    private final T employer;

    @Override
    public void display(final Player player) {
        final var gui = new ClickableInventory(54, guiName);

        final var toggleButton = new FunctionalClickable<>(toggledItemstack(employer.isToggled()), employer, t -> {
            t.toggle();
            gui.updateInventory();
        });

        gui.setSlot(toggleButton, 0);
        gui.showInventory(player);
    }

    public ItemStack toggledItemstack(final boolean isToggled) {
        final Material material = isToggled ? Material.GREEN_WOOL : Material.RED_WOOL;
        final String textDisplay = isToggled ? "Active" : "Inactive";
        final ItemStack item = ItemStack.of(material);
        item.getItemMeta().displayName(Component.text(textDisplay));
        return item;
    }
}
