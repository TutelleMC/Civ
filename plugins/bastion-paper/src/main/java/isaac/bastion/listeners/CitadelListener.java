package isaac.bastion.listeners;

import isaac.bastion.Bastion;
import isaac.bastion.BastionBlock;
import isaac.bastion.BastionType;
import isaac.bastion.Permissions;
import isaac.bastion.manager.BastionBlockManager;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import vg.civcraft.mc.citadel.events.ReinforcementChangeTypeEvent;
import vg.civcraft.mc.citadel.events.ReinforcementCreationEvent;
import vg.civcraft.mc.citadel.events.ReinforcementGroupChangeEvent;
import vg.civcraft.mc.citadel.events.ReinforcementRepairEvent;
import vg.civcraft.mc.namelayer.permission.PermissionType;

public class CitadelListener implements Listener {

    private BastionBlockManager blockManager;

    public CitadelListener() {
        this.blockManager = Bastion.getBastionManager();
    }

    @EventHandler(ignoreCancelled = true)
    public void onReinforcementCreation(ReinforcementCreationEvent event) {
        Set<BastionBlock> preblocking = blockManager.getBlockingBastionsWithoutPermission(event.getReinforcement().getLocation(),
            event.getPlayer().getUniqueId(), PermissionType.getPermission(Permissions.BASTION_PLACE));
        for (BastionBlock bastion : preblocking) {
            BastionType type = bastion.getType();
            if (type.isBlockReinforcements()) {
                event.setCancelled(true);
                if (!Bastion.getSettingManager().getIgnorePlacementMessages(event.getPlayer().getUniqueId())) {
                    event.getPlayer().sendMessage(ChatColor.RED + "Bastion prevented reinforcement");
                }
                blockManager.erodeFromPlace(event.getPlayer(), preblocking);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onReinforcementRepair(ReinforcementRepairEvent event) {
        BastionBlock bastionBlock = Bastion.getBastionStorage().getBastionBlock(event.getReinforcement().getLocation());
        if (bastionBlock == null) return;
        bastionBlock.setPlaced(System.currentTimeMillis());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onReinforcementRepair(ReinforcementChangeTypeEvent event) {
        BastionBlock bastionBlock = Bastion.getBastionStorage().getBastionBlock(event.getReinforcement().getLocation());
        if (bastionBlock == null) return;
        bastionBlock.setPlaced(System.currentTimeMillis());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onReinforcementRepair(ReinforcementGroupChangeEvent event) {
        BastionBlock bastionBlock = Bastion.getBastionStorage().getBastionBlock(event.getReinforcement().getLocation());
        if (bastionBlock == null) return;
        bastionBlock.setPlaced(System.currentTimeMillis());
    }
}
