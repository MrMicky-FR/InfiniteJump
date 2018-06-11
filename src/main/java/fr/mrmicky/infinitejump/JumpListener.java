package fr.mrmicky.infinitejump;

import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.*;

/**
 * @author MrMicky
 */
public class JumpListener implements Listener {

    private List<UUID> cooldown = new ArrayList<>();
    private Map<UUID, Integer> jumpleft = new HashMap<>();

    private InfiniteJump m;

    JumpListener(InfiniteJump m) {
        this.m = m;

        m.getServer().getScheduler().runTaskTimer(m, () -> m.getJumps().forEach(uuid -> {
            Player p = m.getServer().getPlayer(uuid);
            if (p != null && !cooldown.contains(uuid) && p.isOnGround()) {
                p.setAllowFlight(true);
                jumpleft.remove(p.getUniqueId());
            }
        }), 10, 10);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (p.hasPermission("infinitejump.use")) {
            m.getJumps().add(p.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        m.getJumps().remove(uuid);
        jumpleft.remove(uuid);
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();

        if ((p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE) && m.getJumps().contains(uuid)
                && !cooldown.contains(uuid) && p.hasPermission("infinitejump.use")) {
            e.setCancelled(true);
            int left = (jumpleft.containsKey(uuid) ? jumpleft.get(uuid) : m.getMaxJump(p)) - 1;
            if (left < 2) {
                p.setAllowFlight(false);
                p.setFlying(false);
                jumpleft.remove(uuid);
            } else {
                jumpleft.put(uuid, left);
            }

            p.setVelocity(p.getLocation().getDirection().multiply(m.getConfig().getDouble("Velocity")));

            if (m.getConfig().getBoolean("Sound.Enable")) {
                p.getWorld().playSound(p.getLocation(), Sound.valueOf(m.getConfig().getString("Sound.Sound")),
                        (float) m.getConfig().getDouble("Sound.Volume"),
                        (float) m.getConfig().getDouble("Sound.Pitch"));
            }

            if (m.getConfig().getBoolean("Particles.Enable")) {
                m.spawnParticle(p.getLocation(), m.getConfig().getString("Particles.Particle"),
                        m.getConfig().getInt("Particles.Amount"));
            }

            int c = m.getConfig().getInt("Cooldown");
            if (c != 0) {
                cooldown.add(uuid);
                m.getServer().getScheduler().runTaskLater(m, () -> cooldown.remove(uuid), c);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntityType() == EntityType.PLAYER && e.getCause() == DamageCause.FALL &&
                m.getConfig().getBoolean("RemoveFallDamages") && m.getJumps().contains(e.getEntity().getUniqueId())) {
                e.setCancelled(true);
        }
    }
}
