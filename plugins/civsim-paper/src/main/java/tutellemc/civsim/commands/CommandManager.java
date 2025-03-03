package tutellemc.civsim.commands;

import tutellemc.civsim.CivSim;
import tutellemc.civsim.services.NodeService;

public class CommandManager extends vg.civcraft.mc.civmodcore.commands.CommandManager {
    private final NodeService nodeService;
    /**
     * Creates a new command manager for Aikar based commands and tab completions.
     *
     */
    public CommandManager(final CivSim plugin, final NodeService nodeService) {
        super(plugin);
        this.nodeService = nodeService;
    }

    @Override
    public void registerCommands() {
        registerCommand(new MineCommand(nodeService));
    }

    @Override
    public CivSim getPlugin() {
        return (CivSim) super.getPlugin();
    }
}
