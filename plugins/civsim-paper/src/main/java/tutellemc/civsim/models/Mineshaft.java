package tutellemc.civsim.models;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter
@RequiredArgsConstructor
public class Mineshaft implements Node, Employer, Toggleable {

    private final int maximumAvailableJobs = 100;
    private final Inventory inventory;
    private final ItemStack offeredWage = new ItemStack(Material.RAW_IRON, 1);
    private final Location location;
    private int numberOfEmployees = 0;
    private boolean isActive = true;

    public List<ItemStack> getOutput() {
        return List.of(ItemStack.of(Material.RAW_IRON, 10 * numberOfEmployees));
    }

    @Override
    public boolean isToggled() {
        return hasEmployees() && isActive;
    }

    @Override
    public boolean toggle() {
        isActive = !isActive;
        return isActive;
    }

    @Override
    public ItemStack getOfferedWage() {
        return offeredWage;
    }

    @Override
    public void hire(int numberToHire) {
        numberOfEmployees += numberToHire;
    }
}
