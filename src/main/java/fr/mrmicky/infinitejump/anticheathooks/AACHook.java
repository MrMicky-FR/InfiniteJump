package fr.mrmicky.infinitejump.anticheathooks;

import fr.mrmicky.infinitejump.InfiniteJump;
import me.konsolas.aac.api.HackType;
import me.konsolas.aac.api.PlayerViolationEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AACHook implements Listener {

    private final InfiniteJump plugin;

    public AACHook(InfiniteJump plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
        plugin.getLogger().info("AAC hook enabled");
    }

    @EventHandler
    public void onViolation(PlayerViolationEvent e) {
        HackType hackType = e.getHackType();

        if (hackType != HackType.MOVE && hackType != HackType.NOFALL) {
            return;
        }

        if (plugin.getJumpManager().hasRecentJump(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
}
