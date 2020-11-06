package fr.mrmicky.infinitejump.anticheathooks;

import fr.mrmicky.infinitejump.InfiniteJump;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import rip.reflex.api.Cheat;
import rip.reflex.api.event.ReflexCheckEvent;

public class ReflexHook implements Listener {

    private final InfiniteJump plugin;

    public ReflexHook(InfiniteJump plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
        plugin.getLogger().info("Reflex hook enabled");
    }

    @EventHandler
    public void onViolation(ReflexCheckEvent e) {
        Cheat cheat = e.getCheat();

        if (cheat == Cheat.Speed && plugin.getJumpManager().hasRecentJump(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
}
