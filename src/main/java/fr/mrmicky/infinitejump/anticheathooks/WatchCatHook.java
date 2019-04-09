package fr.mrmicky.infinitejump.anticheathooks;

import fr.mrmicky.infinitejump.InfiniteJump;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.WatchCat.api.CheatType;
import xyz.WatchCat.api.PlayerCheatEvent;

/**
 * @author MrMicky
 */
public class WatchCatHook implements Listener {

    private final InfiniteJump plugin;

    public WatchCatHook(InfiniteJump plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
        plugin.getLogger().info("WatchCat hook enabled");
    }

    @EventHandler
    public void onCheat(PlayerCheatEvent e) {
        if (e.getType() == CheatType.IrregularMovement && plugin.getJumpManager().isActive(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
}
