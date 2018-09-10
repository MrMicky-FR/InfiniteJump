package fr.mrmicky.infinitejump;

import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.*;

import java.util.UUID;

/**
 * @author MrMicky
 */
public class JumpListener implements Listener {


    private InfiniteJump m;

    JumpListener(InfiniteJump m) {
        this.m = m;

        // for reloads
        m.getServer().getOnlinePlayers().forEach(this::handleJoin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent e) {
        m.getServer().getScheduler().runTask(m, () -> handleJoin(e.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        m.getJumpManager().disable(e.getPlayer());
    }

    @EventHandler
    public void onPlayerWorldChanged(PlayerChangedWorldEvent e) {
        reloadPlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerGamemodeChange(PlayerGameModeChangeEvent e) {
        m.getServer().getScheduler().runTask(m, () -> reloadPlayer(e.getPlayer()));
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();

        if (!m.getJumpManager().isActive(p)) {
            return;
        }

        m.getJumpManager().getJumpsFull().remove(p.getUniqueId());

        int left = m.getJumpManager().getJumps().getOrDefault(uuid, 0);

        if (left >= 2 && !m.getJumpManager().getCooldown().contains(uuid)) {
            e.setCancelled(true);

            if (--left <= 1) {
                p.setAllowFlight(false);
                p.setFlying(false);

                int c = m.getConfig().getInt("Cooldown");
                if (c > 0) {
                    m.getJumpManager().getCooldown().add(uuid);
                    m.getServer().getScheduler().runTaskLater(m, () -> m.getJumpManager().getCooldown().remove(uuid), c);
                }
            }

            m.getJumpManager().getJumps().put(uuid, left);

            p.setVelocity(p.getLocation().getDirection().multiply(m.getConfig().getDouble("Velocity")).setY(m.getConfig().getDouble("VelcocityUp", 1.0)));

            if (m.getConfig().getBoolean("Sound.Enable")) {
                p.getWorld().playSound(p.getLocation(), Sound.valueOf(m.getConfig().getString("Sound.Sound")),
                        (float) m.getConfig().getDouble("Sound.Volume"),
                        (float) m.getConfig().getDouble("Sound.Pitch"));
            }

            if (m.getConfig().getBoolean("Particles.Enable")) {
                m.spawnParticles(p, p.getLocation(), m.getConfig().getString("Particles.Particle"), m.getConfig().getInt("Particles.Amount"));
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntityType() == EntityType.PLAYER && e.getCause() == DamageCause.FALL &&
                m.getConfig().getBoolean("RemoveFallDamages") && m.getJumpManager().getJumps().containsKey(e.getEntity().getUniqueId())) {
            e.setCancelled(true);
        }
    }

    private void handleJoin(Player p) {
        if (m.getConfig().getBoolean("EnableOnJoin")) {
            verifyEnable(p);
        }
    }

    private void verifyEnable(Player p) {
        if (p.hasPermission("infinitejump.use")) {
            m.getJumpManager().enable(p);
        }
    }

    private void reloadPlayer(Player p) {
        if (m.getJumpManager().getEnabledPlayers().contains(p.getUniqueId())) {
            m.getJumpManager().disable(p);
            verifyEnable(p);
        }
    }
}
