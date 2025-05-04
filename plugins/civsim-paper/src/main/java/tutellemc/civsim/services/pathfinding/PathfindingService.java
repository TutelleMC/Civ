package tutellemc.civsim.services.pathfinding;

import de.metaphoriker.pathetic.api.factory.PathfinderFactory;
import de.metaphoriker.pathetic.api.factory.PathfinderInitializer;
import de.metaphoriker.pathetic.api.pathing.Pathfinder;
import de.metaphoriker.pathetic.api.pathing.configuration.PathfinderConfiguration;
import de.metaphoriker.pathetic.api.pathing.result.PathfinderResult;
import de.metaphoriker.pathetic.bukkit.PatheticBukkit;
import de.metaphoriker.pathetic.bukkit.initializer.BukkitPathfinderInitializer;
import de.metaphoriker.pathetic.bukkit.mapper.BukkitMapper;
import de.metaphoriker.pathetic.bukkit.provider.LoadingNavigationPointProvider;
import de.metaphoriker.pathetic.engine.factory.AStarPathfinderFactory;
import org.bukkit.Location;
import tutellemc.civsim.CivSim;
import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Handles pathfinding
 */
public class PathfindingService {

    private Pathfinder pathfinder;

    public void initialize() {
        PatheticBukkit.initialize(CivSim.getInstance());
        final PathfinderFactory factory = new AStarPathfinderFactory();
        final PathfinderInitializer initializer = new BukkitPathfinderInitializer();
        final PathfinderConfiguration config = PathfinderConfiguration.builder()
            .provider(new LoadingNavigationPointProvider())
            .fallback(true)
            .build();
        this.pathfinder = factory.createPathfinder(config, initializer);
    }

    public CompletionStage<PathfinderResult> findSeaBasedPath(final Location start, final Location end) {
        final var internalStart = BukkitMapper.toPathPosition(start);
        final var internalEnd = BukkitMapper.toPathPosition(end);
        return pathfinder.findPath(internalStart, internalEnd, List.of(new SailableFilter()));
    }
}
