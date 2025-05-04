package tutellemc.civsim.services.pathfinding;

import de.metaphoriker.pathetic.api.pathing.filter.PathFilter;
import de.metaphoriker.pathetic.api.pathing.filter.PathValidationContext;
import de.metaphoriker.pathetic.api.provider.NavigationPointProvider;
import de.metaphoriker.pathetic.api.wrapper.PathPosition;
import de.metaphoriker.pathetic.bukkit.provider.BukkitNavigationPoint;
import org.bukkit.Material;

/**
 * Checks if the block is water with a minimum depth of 2
 */
public class SailableFilter implements PathFilter {

    @Override
    public boolean filter(final PathValidationContext pathValidationContext) {
        final PathPosition above = pathValidationContext.getPosition().add(0, 1, 0);
        final PathPosition below = pathValidationContext.getPosition().subtract(0, 1, 0);
        final PathPosition minimumDepthPosition = pathValidationContext.getPosition().subtract(0, 2, 0);
        final NavigationPointProvider navigationPointProvider = pathValidationContext.getNavigationPointProvider();

        var footHeightPosition = (BukkitNavigationPoint) navigationPointProvider.getNavigationPoint(pathValidationContext.getPosition());
        var headHeightPosition = (BukkitNavigationPoint) navigationPointProvider.getNavigationPoint(above);

        var blockWalkedOnPosition = (BukkitNavigationPoint) navigationPointProvider.getNavigationPoint(below);
        var minimumDepthBlock = (BukkitNavigationPoint) navigationPointProvider.getNavigationPoint(minimumDepthPosition);
        return footHeightPosition.isTraversable() &&
            headHeightPosition.isTraversable() &&
            Material.WATER.equals(blockWalkedOnPosition.getMaterial()) &&
            Material.WATER.equals(minimumDepthBlock.getMaterial());
    }
}
