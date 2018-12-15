package fr.mrmicky.infinitejump.anticheathooks;

import fr.mrmicky.infinitejump.InfiniteJump;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.WatchCat.api.CheatType;
import xyz.WatchCat.api.PlayerCheatEvent;

/**
 * @author MrMicky
 */
public class WatchCatHook implements Listener {

    private InfiniteJump m;

    public WatchCatHook(InfiniteJump m) {
        this.m = m;

        m.getServer().getPluginManager().registerEvents(this, m);
        m.getLogger().info("WatchCat hook enabled");
    }

    @EventHandler
    public void onCheat(PlayerCheatEvent e) {
        if (e.getType() == CheatType.IrregularMovement && m.getJumpManager().isActive(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
}
