package fr.mrmicky.infinitejump;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author MrMicky
 */
public class CommandInfiniteJump implements TabExecutor {

    private final InfiniteJump plugin;

    public CommandInfiniteJump(InfiniteJump plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("infinitejump.reload")) {
            plugin.reloadConfig();
            sender.sendMessage(ChatColor.YELLOW + "Config reloaded");
            return true;
        }

        if (args[0].equalsIgnoreCase("toggle") && sender.hasPermission("infinitejump.use")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command");
                return true;
            }

            Player player = (Player) sender;

            if (plugin.getJumpManager().getEnabledPlayers().contains(player.getUniqueId())) {
                plugin.getJumpManager().disable(player);
                player.sendMessage(getConfigMessage("Disabled"));

            } else {
                plugin.getJumpManager().enable(player);
                player.sendMessage(getConfigMessage("Activated"));
            }

            return true;
        }

        sendUsage(sender);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();

            if (sender.hasPermission("infinitejump.use") && sender instanceof Player) {
                completions.add("toggle");
            }

            if (sender.hasPermission("infinitejump.reload")) {
                completions.add("reload");
            }

            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
        }

        return Collections.emptyList();
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(color("&bInfiniteJump v" + plugin.getDescription().getVersion() + " &7by &bMrMicky&7."));

        if (sender.hasPermission("infinitejump.use") && sender instanceof Player) {
            sender.sendMessage(color("&7- &b/infinitejump toggle"));
        }

        if (sender.hasPermission("infinitejump.reload")) {
            sender.sendMessage(color("&7- &b/infinitejump reload"));
        }
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    private String getConfigMessage(String key) {
        return color(plugin.getConfig().getString("Messages." + key));
    }
}
