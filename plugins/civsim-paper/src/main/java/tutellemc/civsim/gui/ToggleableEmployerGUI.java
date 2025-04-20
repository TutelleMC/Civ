package tutellemc.civsim.gui;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import tutellemc.civsim.gui.components.DisplayClickable;
import tutellemc.civsim.gui.components.FunctionalClickable;
import tutellemc.civsim.models.Employer;
import tutellemc.civsim.models.Toggleable;
import vg.civcraft.mc.civmodcore.inventory.gui.ClickableInventory;
import vg.civcraft.mc.civmodcore.inventory.items.ItemUtils;

@RequiredArgsConstructor
public class ToggleableEmployerGUI<T extends Employer & Toggleable> implements GUI {

    private final String guiName;
    private final T employer;

    @Override
    public void display(final Player player) {
        final var gui = new ClickableInventory(54, guiName);

        final var toggleButton = new FunctionalClickable<>(toggledItemstack(employer.isToggled()), employer, t -> {
            t.toggle();
            gui.updateInventory();
        });
        final var numberEmployees = new DisplayClickable(
                numberOfEmployeesInformation(employer.getNumberOfEmployees(), employer.getMaximumAvailableJobs()));

        gui.setSlot(toggleButton, 0);
        gui.setSlot(numberEmployees, 1);
        gui.showInventory(player);
    }

    public ItemStack toggledItemstack(final boolean isToggled) {
        final Material material = isToggled ? Material.GREEN_WOOL : Material.RED_WOOL;
        final Component displayName = isToggled
                ? Component.text("Active").color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true)
                : Component.text("Inactive").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, true);

        final ItemStack item = ItemStack.of(material);
        ItemUtils.setComponentDisplayName(item, displayName);
        return item;
    }

    public ItemStack numberOfEmployeesInformation(final int numberOfEmployees, final int maxEmployees) {
        final ItemStack paper = ItemStack.of(Material.PAPER);
        ItemUtils.setComponentDisplayName(
                paper, Component.text("Number of employees").decoration(TextDecoration.BOLD, true));
        ItemUtils.setComponentLore(
                paper, Component.text("%s / %s employed".formatted(numberOfEmployees, maxEmployees)));
        return paper;
    }
}
