package fr.mrmicky.infinitejump;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author MrMicky
 */
public class CommandInfiniteJump implements CommandExecutor, TabCompleter {

    private InfiniteJump m;

    CommandInfiniteJump(InfiniteJump m) {
        this.m = m;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
        } else if (args[0].equalsIgnoreCase("about") || args[0].equalsIgnoreCase("info")) {
            sender.sendMessage("§b" + m.getName() + "§7 by §bMrMicky §7version §b" + m.getDescription().getVersion());
            sender.sendMessage("§7Download: §b" + m.getDescription().getWebsite());
        } else if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("infinitejump.reload")) {
            m.reloadConfig();
            sender.sendMessage("§aConfig reloaded");
        } else if (args[0].equalsIgnoreCase("toggle") && sender.hasPermission("infinitejump.use")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (m.getJumps().remove(p.getUniqueId())) {
                    p.sendMessage(getConfigMessage("Disabled"));
                    p.setAllowFlight(false);
                } else {
                    m.getJumps().add(p.getUniqueId());
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
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        if (args.length == 1) {
            List<String> futurCompletions = new ArrayList<>();
            List<String> completion = new ArrayList<>();

            futurCompletions.add("about");

            if (sender.hasPermission("infinitejump.use")) {
                futurCompletions.add("toggle");
            }
            if (sender.hasPermission("infinitejump.reload")) {
                futurCompletions.add("reload");
            }

            StringUtil.copyPartialMatches(args[0], futurCompletions, completion);
            return completion;
        }
        return Collections.emptyList();
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage("§b-= §7Infinite Jump §b=-");
        sender.sendMessage("§7- §b/ijump about");
        if (sender.hasPermission("infinitejump.use")) {
            sender.sendMessage("§7- §b/ijump toggle");
        }
        if (sender.hasPermission("infinitejump.reload")) {
            sender.sendMessage("§7- §b/ijump reload");
        }
    }

    private String getConfigMessage(String key) {
        return ChatColor.translateAlternateColorCodes('&', m.getConfig().getString("Messages." + key));
    }
}
