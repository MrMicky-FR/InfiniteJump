package fr.mrmicky.infinitejump;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author MrMicky
 */
public class CommandInfiniteJump implements CommandExecutor {

    private InfiniteJump m;

    CommandInfiniteJump(InfiniteJump m) {
        this.m = m;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (args.length == 0 || !sender.hasPermission("infinitejump.use")) {
            sender.sendMessage("§b" + m.getName() + "§7 by §bMrMicky §7version §b" + m.getDescription().getVersion());
            sender.sendMessage("§bDownload: §7" + m.getDescription().getWebsite());
        } else if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("infinitejump.reload")) {
            m.reloadConfig();
            m.verifyConfig();
            sender.sendMessage("§aConfig reloaded");
        } else if (args[0].equalsIgnoreCase("toggle")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (m.getJumps().remove(p.getUniqueId())) {
                    p.sendMessage(getConfigMessage("Disabled"));
                    p.setAllowFlight(false);
                } else {
                    m.getJumps().add(p.getUniqueId());
                    p.sendMessage(getConfigMessage("Activated"));
                }
            }
        } else {
            sender.sendMessage("§b/ijump toggle : §7Toggle the multiple jump");
            if (sender.hasPermission("infinitejump.reload")) {
                sender.sendMessage("§b/ijump reload : §7Reload the config");
            }
        }
        return true;
    }

    private String getConfigMessage(String key) {
        return ChatColor.translateAlternateColorCodes('&', m.getConfig().getString("Messages." + key));
    }
}
