package tutellemc.civsim.models;

import org.bukkit.inventory.ItemStack;

public interface Employer {
    int getNumberOfEmployees();

    int getMaximumAvailableJobs();

    ItemStack getOfferedWage();

    default boolean hasEmployees() {
        return getNumberOfEmployees() > 0;
    }

    default boolean canHire() {
        return getNumberOfEmployees() < getMaximumAvailableJobs();
    }

    default int numberOfVacantJobs() {
        return getMaximumAvailableJobs() - getNumberOfEmployees();
    }

    void hire(int numberToHire);
}
