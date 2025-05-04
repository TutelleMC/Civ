package tutellemc.civsim.commands;

import tutellemc.civsim.CivSim;
import tutellemc.civsim.services.NodeService;
import tutellemc.civsim.services.ShopsService;
import tutellemc.civsim.services.pathfinding.PathfindingService;

public class CommandManager extends vg.civcraft.mc.civmodcore.commands.CommandManager {

    private final NodeService nodeService;
    private final ShopsService shopsService;
    private final PathfindingService pathfindingService;

    public CommandManager(final CivSim plugin, final NodeService nodeService, final ShopsService shopsService, final PathfindingService pathfindingService) {
        super(plugin);
        this.nodeService = nodeService;
        this.shopsService = shopsService;
        this.pathfindingService = pathfindingService;
    }

    @Override
    public void registerCommands() {
        registerCommand(new MineCommand(nodeService, shopsService, pathfindingService));
    }

    @Override
    public CivSim getPlugin() {
        return (CivSim) super.getPlugin();
    }
}
