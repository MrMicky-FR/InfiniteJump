package fr.mrmicky.infinitejump.anticheathooks;

import fr.mrmicky.infinitejump.InfiniteJump;
import me.vagdedes.spartan.api.PlayerViolationEvent;
import me.vagdedes.spartan.system.Enums.HackType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author MrMicky
 */
public class SpartanHook implements Listener {

    private InfiniteJump m;

    public SpartanHook(InfiniteJump m) {
        this.m = m;
        m.getServer().getPluginManager().registerEvents(this, m);
        m.getLogger().info("Spartan hook enabled");
    }

    @EventHandler
    public void onViolation(PlayerViolationEvent e) {
        if (e.getHackType() == HackType.IrregularMovements && m.getJumpManager().isActive(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
}
