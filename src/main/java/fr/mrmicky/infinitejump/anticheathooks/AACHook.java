package fr.mrmicky.infinitejump.anticheathooks;

import fr.mrmicky.infinitejump.InfiniteJump;
import me.konsolas.aac.api.HackType;
import me.konsolas.aac.api.PlayerViolationEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author MrMicky
 */
public class AACHook implements Listener {

    private InfiniteJump m;

    public AACHook(InfiniteJump m) {
        this.m = m;
        m.getServer().getPluginManager().registerEvents(this, m);
        m.getLogger().info("AAC hook enabled");
    }

    @EventHandler
    public void onViolation(PlayerViolationEvent e) {
        if (e.getHackType() == HackType.FLY && m.getJumpManager().isActive(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
}
