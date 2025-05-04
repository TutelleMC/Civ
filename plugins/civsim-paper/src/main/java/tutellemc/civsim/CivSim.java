package tutellemc.civsim;

import java.time.Duration;
import java.util.List;
import java.util.logging.Logger;
import lombok.Getter;
import net.kyori.adventure.util.Ticks;
import tutellemc.civsim.commands.CommandManager;
import tutellemc.civsim.glue.ItemExchangeGlue;
import tutellemc.civsim.services.NodeService;
import tutellemc.civsim.services.ShopsService;
import tutellemc.civsim.services.pathfinding.PathfindingService;
import tutellemc.civsim.tasks.HiringTask;
import tutellemc.civsim.tasks.ProductionTask;
import vg.civcraft.mc.civmodcore.ACivMod;
import vg.civcraft.mc.civmodcore.utilities.DependencyGlue;

public final class CivSim extends ACivMod implements AutoCloseable {

    private static final long FIVE_MINUTES_TICKS = Duration.ofMinutes(5).toSeconds() * Ticks.TICKS_PER_SECOND;

    @Getter
    private static CivSim instance;

    private final NodeService nodeService = new NodeService();
    private final ShopsService shopsService = new ShopsService();
    private final PathfindingService pathfindingService = new PathfindingService();
    private CommandManager commands;

    private final List<DependencyGlue> glues = List.of(new ItemExchangeGlue(this, shopsService));

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        this.glues.forEach(DependencyGlue::registerGlue);

        pathfindingService.initialize();

        final var scheduler = this.getServer().getScheduler();
        scheduler.runTaskTimer(instance, new HiringTask(nodeService, shopsService), 0, FIVE_MINUTES_TICKS);
        scheduler.runTaskTimer(instance, new ProductionTask(nodeService), 0, FIVE_MINUTES_TICKS);

        commands = new CommandManager(this, nodeService, shopsService, pathfindingService);
        commands.init();

        getLogger().info("CivSim started up");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (commands != null) {
            commands.reset();
            commands = null;
        }
        this.glues.forEach(DependencyGlue::resetGlue);
    }

    @Override
    public void close() {
    }

    public static Logger log() {
        return instance.getLogger();
    }
}
