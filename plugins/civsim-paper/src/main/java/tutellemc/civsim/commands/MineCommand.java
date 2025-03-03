package tutellemc.civsim.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import tutellemc.civsim.models.Mineshaft;
import tutellemc.civsim.services.NodeService;

@CommandAlias(MineCommand.CIVSIM_ALIAS)
@RequiredArgsConstructor
public class MineCommand extends BaseCommand {
    public static final String CIVSIM_ALIAS = "civsim|cs";
    private final NodeService nodeService;

    @Default
    @Description("It just turns the chest you're looking at into a mineshaft! :D")
    public void getMine(final Player player) {
        final var block = player.getTargetBlock(null, 5);
        if (!block.getType().equals(Material.CHEST) || !(block instanceof Chest chest)) {
            player.sendMessage("Only chests can be turned into mineshafts");
            return;
        }

        final var mineshaft = new Mineshaft(chest.getInventory());
        nodeService.registerNode(mineshaft);
    }
}
