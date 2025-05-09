package tutellemc.civsim.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import de.metaphoriker.pathetic.bukkit.mapper.BukkitMapper;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import tutellemc.civsim.CivSim;
import tutellemc.civsim.Utils;
import tutellemc.civsim.gui.ToggleableEmployerGUI;
import tutellemc.civsim.models.Mineshaft;
import tutellemc.civsim.services.NodeService;
import tutellemc.civsim.services.ShopsService;
import tutellemc.civsim.services.pathfinding.PathfindingService;
import tutellemc.civsim.tasks.HiringTask;
import tutellemc.civsim.tasks.ProductionTask;

@SuppressWarnings("unused")
@CommandAlias(MineCommand.CIVSIM_ALIAS)
@RequiredArgsConstructor
public class MineCommand extends BaseCommand {

    public static final String CIVSIM_ALIAS = "civsim|cs";
    public static final String DEBUG = "debug";
    private final NodeService nodeService;
    private final ShopsService shopsService;
    private final PathfindingService pathfindingService;

    private Location start;

    @Default
    @Description("It just turns the chest you're looking at into a mineshaft! :D")
    public void getMine(final Player player) {
        CivSim.log().info("%s tried to create a mine at %s".formatted(player, player.getLocation()));
        final Block block = player.getTargetBlock(null, 5);
        final Inventory inventory = Utils.getInventoryFromBlock(block);

        if (inventory == null) {
            player.sendMessage("%s does not have an inventory".formatted(block.getType()));
            return;
        }

        CivSim.log()
            .info("%s created a mine at %s with contents %s"
                .formatted(player, block.getLocation(), Utils.prettyPrintInventory(inventory)));

        final var mineshaft = new Mineshaft(inventory, block.getLocation());
        nodeService.registerNode(mineshaft);
        player.sendMessage("Registered mine at %s".formatted(block.getLocation()));
    }

    @Subcommand(DEBUG)
    public void debug(final Player player) {
        final var block = player.getTargetBlock(null, 5);
        if (block.getType().equals(Material.AIR)) {
            return;
        }
        final var inventory = Utils.getInventoryFromBlock(block);
        if (inventory != null) {
            player.sendMessage("Container holds: %s"
                .formatted(Utils.prettyPrintInventory(((Chest) block.getState()).getInventory())));
        }
        nodeService.getNodes().stream()
            .filter(v -> v.getLocation().equals(block.getLocation()))
            .findAny()
            .ifPresent(mine -> {
                player.sendMessage("Mineshaft %s has %s workers and inv with %s and is located at %s"
                    .formatted(
                        mine,
                        mine.getNumberOfEmployees(),
                        Utils.prettyPrintInventory(mine.getInventory()),
                        mine.getLocation()));
                new ToggleableEmployerGUI<>("Mineshaft", mine).display(player);
            });
    }

    @Subcommand("run_hire_task")
    public void runHireTask(final Player player) {
        new HiringTask(nodeService, shopsService).runTask();
    }

    @Subcommand("run_production_task")
    public void runProductionTask(final Player player) {
        new ProductionTask(nodeService).runTask();
    }

    @Subcommand("startPathfind")
    public void startPathfinding(final Player player) {
        this.start = player.getLocation();
    }

    @Subcommand("endPathfind")
    public void endPathfinding(final Player player) {
        if (start == null) {
            player.sendMessage("Start of path was not defined");
            return;
        }
        pathfindingService.findSeaBasedPath(start, player.getLocation()).thenAccept(result -> {
            player.sendMessage("State: " + result.getPathState().name());
            player.sendMessage("Path length: " + result.getPath().length());
            if (result.successful() || result.hasFallenBack()) {
                result
                    .getPath()
                    .forEach(
                        position -> {
                            Location location = BukkitMapper.toLocation(position);
                            player.sendBlockChange(
                                location, Material.YELLOW_STAINED_GLASS.createBlockData());
                        });
            } else {
                player.sendMessage("Path not found!");
            }
        });
//        final Villager spawnedVillager = start.getWorld().spawn(start, Villager.class);
//        Bukkit.getScheduler()
//                .runTaskLater(
//                        CivSim.getInstance(),
//                        () -> {
//                            Bukkit.getMobGoals().removeAllGoals(spawnedVillager);
//                            final boolean result =
//                                    spawnedVillager.getPathfinder().moveTo(player.getLocation(), 1);
//                            player.sendMessage(Component.text("Pathfinding %s to %s result: %s"
//                                    .formatted(
//                                            Utils.printCoords(start),
//                                            Utils.printCoords(player.getLocation()),
//                                            result)));
//                        },
//                        60L);
    }
}
