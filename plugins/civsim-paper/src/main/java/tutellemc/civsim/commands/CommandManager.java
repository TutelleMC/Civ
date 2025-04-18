package tutellemc.civsim.commands;

import tutellemc.civsim.CivSim;
import tutellemc.civsim.services.NodeService;
import tutellemc.civsim.services.ShopsService;

public class CommandManager extends vg.civcraft.mc.civmodcore.commands.CommandManager {
    private final NodeService nodeService;
    private final ShopsService shopsService;

    public CommandManager(final CivSim plugin, final NodeService nodeService, final ShopsService shopsService) {
        super(plugin);
        this.nodeService = nodeService;
        this.shopsService = shopsService;
    }

    @Override
    public void registerCommands() {
        registerCommand(new MineCommand(nodeService, shopsService));
    }

    @Override
    public CivSim getPlugin() {
        return (CivSim) super.getPlugin();
    }
}
