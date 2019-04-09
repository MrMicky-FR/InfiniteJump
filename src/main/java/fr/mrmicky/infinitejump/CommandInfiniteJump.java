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
        } else if (args[0].equalsIgnoreCase("about") || args[0].equalsIgnoreCase("info")) {
            sender.sendMessage("§b" + plugin.getName() + "§7 by §bMrMicky §7version §b" + plugin.getDescription().getVersion());
            sender.sendMessage("§7Download: §b" + plugin.getDescription().getWebsite());
        } else if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("infinitejump.reload")) {
            plugin.reloadConfig();
            sender.sendMessage("§aConfig reloaded");
        } else if (args[0].equalsIgnoreCase("toggle") && sender.hasPermission("infinitejump.use")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (plugin.getJumpManager().getEnabledPlayers().contains(p.getUniqueId())) {
                    plugin.getJumpManager().disable(p);
                    p.sendMessage(getConfigMessage("Disabled"));
                } else {
                    plugin.getJumpManager().enable(p);
                    p.sendMessage(getConfigMessage("Activated"));
                }
            } else {
                sender.sendMessage("§cThis command need to be use by a player");
            }
        } else {
            sendUsage(sender);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();

            completions.add("about");

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
        sender.sendMessage("§b-= §7Infinite Jump §b=-");
        sender.sendMessage("§7- §b/ijump about");
        if (sender.hasPermission("infinitejump.use") && sender instanceof Player) {
            sender.sendMessage("§7- §b/ijump toggle");
        }
        if (sender.hasPermission("infinitejump.reload")) {
            sender.sendMessage("§7- §b/ijump reload");
        }
    }

    private String getConfigMessage(String key) {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Messages." + key));
    }
}
