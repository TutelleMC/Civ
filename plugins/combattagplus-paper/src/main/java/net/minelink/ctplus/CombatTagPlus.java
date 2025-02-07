package net.minelink.ctplus;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;


import java.util.UUID;
import net.minelink.ctplus.compat.base.NpcNameGeneratorFactory;
import net.minelink.ctplus.compat.base.NpcPlayerHelper;
import net.minelink.ctplus.hook.Hook;
import net.minelink.ctplus.hook.HookManager;
import net.minelink.ctplus.listener.ForceFieldListener;
import net.minelink.ctplus.listener.InstakillListener;
import net.minelink.ctplus.listener.NpcListener;
import net.minelink.ctplus.listener.PlayerListener;
import net.minelink.ctplus.listener.TagListener;
import net.minelink.ctplus.task.ForceFieldTask;
import net.minelink.ctplus.task.SafeLogoutTask;
import net.minelink.ctplus.task.TagUpdateTask;
import net.minelink.ctplus.util.BarUtils;
import net.minelink.ctplus.util.ReflectionUtils;
import net.minelink.ctplus.util.Version;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class CombatTagPlus extends JavaPlugin {

    private final PlayerCache playerCache = new PlayerCache();

    private Settings settings;

    private HookManager hookManager;

    private TagManager tagManager;

    private NpcPlayerHelper npcPlayerHelper;

    private NpcManager npcManager;

    public PlayerCache getPlayerCache() {
        return playerCache;
    }

    public Settings getSettings() {
        return settings;
    }

    public HookManager getHookManager() {
        return hookManager;
    }

    public TagManager getTagManager() {
        return tagManager;
    }

    public NpcPlayerHelper getNpcPlayerHelper() {
        return npcPlayerHelper;
    }

    public NpcManager getNpcManager() {
        return npcManager;
    }

    @Override
    public void onEnable() {
        // Load settings
        saveDefaultConfig();

        settings = new Settings(this);
        if (settings.isOutdated()) {
            settings.update();
            getLogger().info("Configuration file has been updated.");
        }

        // Disable plugin if version compatibility check fails
        if (!checkVersionCompatibility()) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize plugin state
        hookManager = new HookManager(this);
        tagManager = new TagManager(this);
        if (npcPlayerHelper != null) {
            npcManager = new NpcManager(this);
        }

        NpcNameGeneratorFactory.setNameGenerator(new NpcNameGeneratorImpl(this));

        integrateFactions();
        integrateWorldGuard();

        BarUtils.init();

        // Build player cache from currently online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            getPlayerCache().addPlayer(player);
        }

        // Register event listeners
        Bukkit.getPluginManager().registerEvents(new ForceFieldListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InstakillListener(this), this);

        if (getNpcManager() != null) {
            Bukkit.getPluginManager().registerEvents(new NpcListener(this), this);
        }

        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new TagListener(this), this);

        // Anti-SafeZone task
        ForceFieldTask.run(this);

        // Periodic task for purging unused data
        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                getTagManager().purgeExpired();
                TagUpdateTask.purgeFinished();
                SafeLogoutTask.purgeFinished();
            }
        }, 3600, 3600);
    }

    @Override
    public void onDisable() {
        TagUpdateTask.cancelTasks(this);
    }

    private boolean checkVersionCompatibility() {
        // Load NMS compatibility helper class
        Class<?> helperClass;
        try {
            helperClass = Class.forName("net.minelink.ctplus.nms.NpcPlayerHelperImpl");
        } catch (ClassNotFoundException exception) {
            exception.printStackTrace();
            return false;
        }

        // Warn about incompatibility and return false indicating failure
        if (helperClass == null) {
            // Always compatible if NPCs aren't being used
            if (settings.instantlyKill() && !settings.alwaysSpawn()) {
                return true;
            }
            getLogger().severe("**VERSION ERROR**");
            getLogger().severe("Server API version detected: " + ReflectionUtils.API_VERSION);
            getLogger().severe("This version of CombatTagPlus is not compatible with your CraftBukkit.");
            return false;
        }

        // Helper class was found
        try {
            // Attempt to create a new helper
            npcPlayerHelper = (NpcPlayerHelper) helperClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            // Fail miserably
            throw new RuntimeException(e);
        }

        // Yay, we're compatible! (hopefully)
        return true;
    }

    private void integrateFactions() {
        if (!getSettings().useFactions()) {
            return;
        }

        // Determine if Factions is loaded
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Factions");
        if (plugin == null) {
            return;
        }

        Version v;

        try {
            v = new Version(plugin.getDescription().getVersion());
        } catch (IllegalArgumentException e) {
            v = new Version("0.0");
        }

        String version = null;

        if (v.compareTo(new Version("1.6")) < 0) {
            version = "1_6";
        } else if (v.compareTo(new Version("2.7")) > 0) {
            version = "2_7";
        }

        if (version == null) {
            String[] parts = v.toString().split("\\.");
            version = parts[0] + "_" + parts[1];
        }

        // Determine which hook implementation to use
        String className = "net.minelink.ctplus.factions.v" + version + ".FactionsHook";

        try {
            // Create and add FactionsHook
            getHookManager().addHook((Hook) Class.forName(className).newInstance());
        } catch (Exception e) {
            // Something went wrong, chances are it's a newer, incompatible Factions
            getLogger().warning("**WARNING**");
            getLogger().warning("Failed to enable Factions integration due to errors.");
            getLogger().warning("This is most likely due to a newer Factions.");

            // Let's leave a stack trace in console for reporting
            e.printStackTrace();
        }
    }

    private void integrateWorldGuard() {
        if (!getSettings().useWorldGuard()) {
            return;
        }

        // Determine if WorldGuard is loaded
        Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");
        if (plugin == null) {
            return;
        }

        String v = plugin.getDescription().getVersion();

        // Determine which hook implementation to use
        String className = "net.minelink.ctplus.worldguard.v" + v.substring(0, 1) + ".WorldGuardHook";

        try {
            // Create and add WorldGuardHook
            getHookManager().addHook((Hook) Class.forName(className).newInstance());
        } catch (Exception e) {
            // Something went wrong, chances are it's a newer, incompatible WorldGuard
            getLogger().warning("**WARNING**");
            getLogger().warning("Failed to enable WorldGuard integration due to errors.");
            getLogger().warning("This is most likely due to a newer WorldGuard.");

            // Let's leave a stack trace in console for reporting
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equals("ctplusreload")) {
            reloadConfig();
            getSettings().load();
            if (sender instanceof Player) {
                sender.sendMessage(GREEN + getName() + " config reloaded.");
            }

            getLogger().info("Config reloaded by " + sender.getName());
        } else if (cmd.getName().equals("combattagplus")) {
            if (!(sender instanceof Player)) return false;

            UUID uniqueId = ((Player) sender).getUniqueId();
            Tag tag = getTagManager().getTag(uniqueId);
            if (tag == null || tag.isExpired() || !getTagManager().isTagged(uniqueId)) {
                sender.sendMessage(getSettings().getCommandUntagMessage());
                return true;
            }

            String duration = settings.formatDuration(tag.getTagDuration());
            sender.sendMessage(getSettings().getCommandTagMessage().replace("{time}", duration));
        } else if (cmd.getName().equals("ctpluslogout")) {
            if (!(sender instanceof Player)) return false;

            // Do nothing if player is already logging out
            Player player = (Player) sender;
            if (SafeLogoutTask.hasTask(player)) return false;

            // Attempt to start a new logout task
            SafeLogoutTask.run(this, player);
        } else if (cmd.getName().equals("ctplusuntag")) {

            if (args.length < 1) {
                sender.sendMessage(RED + "Please specify a player to untag");
                return true;
            }

            @SuppressWarnings("deprecation")
            Player player = getServer().getPlayer(args[0]);
            if (player == null || getNpcPlayerHelper().isNpc(player)) {
                sender.sendMessage(RED + args[0] + " is not currently online!");
                return true;
            }
            UUID uniqueId = player.getUniqueId();
            if (getTagManager().untag(uniqueId)) {
                sender.sendMessage(GREEN + "Successfully untagged " + player.getName() + ".");
            } else {
                sender.sendMessage(GREEN + player.getName() + " is already untagged.");
            }
        }

        return true;
    }

}
