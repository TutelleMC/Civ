package tutellemc.civsim.models;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter
@RequiredArgsConstructor
public class Mineshaft implements Node, Employer, Toggleable {

    private final int maximumAvailableJobs = 100;
    private final Inventory inventory;
    private ItemStack offeredWage;
    private Vec3 location;
    private int numberOfEmployees = 0;
    private boolean isActive = false;

    public List<ItemStack> getOutput() {
        return List.of(ItemStack.of(Material.RAW_IRON, 10 * numberOfEmployees));
    }

    @Override
    public boolean isActive() {
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
