package fr.mrmicky.infinitejump;

import fr.mrmicky.infinitejump.particle.ParticleUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.UUID;

/**
 * @author MrMicky
 */
public class JumpListener implements Listener {

    private final InfiniteJump plugin;

    public JumpListener(InfiniteJump plugin) {
        this.plugin = plugin;

        // Support reloads
        Bukkit.getOnlinePlayers().forEach(this::handleJoin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent e) {
        plugin.getServer().getScheduler().runTask(plugin, () -> handleJoin(e.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        plugin.getJumpManager().disable(e.getPlayer());
    }

    @EventHandler
    public void onPlayerWorldChanged(PlayerChangedWorldEvent e) {
        reloadPlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerGamemodeChange(PlayerGameModeChangeEvent e) {
        plugin.getServer().getScheduler().runTask(plugin, () -> reloadPlayer(e.getPlayer()));
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();

        if (!plugin.getJumpManager().isActive(p)) {
            return;
        }

        plugin.getJumpManager().getJumpsFull().remove(p.getUniqueId());

        int left = plugin.getJumpManager().getJumps().getOrDefault(uuid, 0);

        if (left >= 2 && !plugin.getJumpManager().getCooldown().contains(uuid)) {
            e.setCancelled(true);

            if (--left <= 1) {
                p.setAllowFlight(false);
                p.setFlying(false);

                int c = plugin.getConfig().getInt("Cooldown");
                if (c > 0) {
                    plugin.getJumpManager().getCooldown().add(uuid);
                    plugin.getServer().getScheduler().runTaskLater(plugin, () -> plugin.getJumpManager().getCooldown().remove(uuid), c);
                }
            } else if (p.isFlying()) {
                p.setFlying(false);
            }

            plugin.getJumpManager().getJumps().put(uuid, left);

            double velocity = plugin.getConfig().getDouble("Velocity");
            double velocityUp = plugin.getConfig().getDouble("VelcocityUp");

            p.setVelocity(p.getLocation().getDirection().multiply(velocity).setY(velocityUp));

            if (plugin.getConfig().getBoolean("Sound.Enable")) {
                p.getWorld().playSound(p.getLocation(), Sound.valueOf(plugin.getConfig().getString("Sound.Sound")),
                        (float) plugin.getConfig().getDouble("Sound.Volume"),
                        (float) plugin.getConfig().getDouble("Sound.Pitch"));
            }

            if (plugin.getConfig().getBoolean("Particles.Enable")) {
                String particle = plugin.getConfig().getString("Particles.Particle");
                int particleCount = plugin.getConfig().getInt("Particles.Amount");

                ParticleUtils.spawnParticles(p, particle, p.getLocation(), particleCount);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntityType() != EntityType.PLAYER || e.getCause() != DamageCause.FALL) {
            return;
        }

        if (plugin.getConfig().getBoolean("RemoveFallDamages") && plugin.getJumpManager().isActive((Player) e.getEntity())) {
            e.setCancelled(true);
        }
    }

    private void handleJoin(Player p) {
        if (plugin.getConfig().getBoolean("EnableOnJoin")) {
            verifyEnable(p);
        }
    }

    private void verifyEnable(Player p) {
        if (p.hasPermission("infinitejump.use")) {
            plugin.getJumpManager().enable(p);
        }
    }

    private void reloadPlayer(Player p) {
        if (plugin.getJumpManager().getEnabledPlayers().contains(p.getUniqueId())) {
            plugin.getJumpManager().disable(p);
            verifyEnable(p);
        }
    }
}
