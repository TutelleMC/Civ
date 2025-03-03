package tutellemc.civsim.tasks;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitTask;
import tutellemc.civsim.CivSim;
import tutellemc.civsim.models.Mineshaft;
import tutellemc.civsim.services.NodeService;

@RequiredArgsConstructor
public class ProductionTask implements Consumer<BukkitTask> {

    private final NodeService nodeService;

    @Override
    public void accept(BukkitTask bukkitTask) {
        CivSim.log().info("Running production task");
        nodeService.getNodes().stream().filter(Mineshaft::isActive).forEach(node -> node.getOutput()
                .forEach(output -> node.getInventory().addItem(output)));
    }
}
