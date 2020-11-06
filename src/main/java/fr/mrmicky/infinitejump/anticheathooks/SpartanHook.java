package fr.mrmicky.infinitejump.anticheathooks;

import fr.mrmicky.infinitejump.InfiniteJump;
import me.vagdedes.spartan.api.PlayerViolationEvent;
import me.vagdedes.spartan.system.Enums.HackType;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SpartanHook implements Listener {

    private final InfiniteJump plugin;

    public SpartanHook(InfiniteJump plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
        plugin.getLogger().info("Spartan hook enabled");
    }

    @EventHandler
    public void onViolation(PlayerViolationEvent e) {
        if (e.getHackType() != HackType.IrregularMovements) {
            return;
        }

        if (plugin.getJumpManager().hasRecentJump(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
}
