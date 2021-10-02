package fr.mrmicky.infinitejump;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
            handleToggle(sender, args);

            return true;
        }

        sendUsage(sender);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            List<String> completions = new ArrayList<>();

            if (sender.hasPermission("infinitejump.use")) {
                completions.add("toggle");
            }

            if (sender.hasPermission("infinitejump.reload")) {
                completions.add("reload");
            }

            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
        }

        if (args[0].equalsIgnoreCase("toggle") && sender.hasPermission("infinitejump.use")) {
            if (args.length == 2) {
                return StringUtil.copyPartialMatches(args[1], Arrays.asList("on", "off"), new ArrayList<>());
            }

            if (args.length == 3 && sender.hasPermission("infinitejump.toggle.others")) {
                return null;
            }
        }

        return Collections.emptyList();
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(color("&bInfiniteJump v" + plugin.getDescription().getVersion() + " &7by &bMrMicky&7."));

        if (sender.hasPermission("infinitejump.toggle.others")) {
            sender.sendMessage(color("&7- &b/infinitejump toggle [on|off] [player]"));
        } else if (sender.hasPermission("infinitejump.use")) {
            sender.sendMessage(color("&7- &b/infinitejump toggle [on|off]"));
        }

        if (sender.hasPermission("infinitejump.reload")) {
            sender.sendMessage(color("&7- &b/infinitejump reload"));
        }
    }

    private void handleToggle(CommandSender sender, String[] args) {
        Player player = null;

        if (args.length >= 3 && sender.hasPermission("infinitejump.toggle.others")) {
            player = Bukkit.getPlayer(args[2]);

            if (player == null) {
                sender.sendMessage(ChatColor.RED + "The player '" + args[2] + "' doesn't exists.");

                return;
            }
        }

        if (player == null) {
            if (!(sender instanceof Player)) {
                sendToggleUsage(sender);

                return;
            }

            player = (Player) sender;
        }

        boolean enable = !plugin.getJumpManager().getEnabledPlayers().contains(player.getUniqueId());

        if (args.length >= 2) {
            if (args[1].equalsIgnoreCase("on")) {
                enable = true;
            } else if (args[1].equalsIgnoreCase("off")) {
                enable = false;
            } else {
                sendToggleUsage(sender);
                return;
            }
        }

        if (enable) {
            plugin.getJumpManager().enable(player);
            sender.sendMessage(getConfigMessage("Activated"));
        } else {
            plugin.getJumpManager().disable(player);
            sender.sendMessage(getConfigMessage("Disabled"));
        }
    }

    private void sendToggleUsage(CommandSender sender) {
        if (sender.hasPermission("infinitejump.toggle.others")) {
            sender.sendMessage(ChatColor.RED + "Usage: /ijump toggle [on|off] [player]");
            return;
        }

        sender.sendMessage(ChatColor.RED + "Usage: /ijump toggle [on|off]");
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    private String getConfigMessage(String key) {
        return color(plugin.getConfig().getString("Messages." + key));
    }
}
