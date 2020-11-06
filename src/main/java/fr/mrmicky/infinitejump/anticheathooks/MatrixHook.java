package fr.mrmicky.infinitejump.anticheathooks;

import fr.mrmicky.infinitejump.InfiniteJump;
import me.rerere.matrix.api.HackType;
import me.rerere.matrix.api.events.PlayerViolationEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MatrixHook implements Listener {

    private final InfiniteJump plugin;

    public MatrixHook(InfiniteJump plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
        plugin.getLogger().info("Matrix hook enabled");
    }

    @EventHandler
    public void onViolation(PlayerViolationEvent e) {
        HackType hackType = e.getHackType();

        if (hackType != HackType.SPEED && hackType != HackType.VELOCITY && hackType != HackType.PHASE) {
            return;
        }

        if (plugin.getJumpManager().hasRecentJump(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
}
