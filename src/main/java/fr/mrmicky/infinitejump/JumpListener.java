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
        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getOnlinePlayers().forEach(this::handleJoin));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent e) {
        plugin.getServer().getScheduler().runTask(plugin, () -> handleJoin(e.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        plugin.getJumpManager().disable(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerWorldChanged(PlayerChangedWorldEvent e) {
        reloadPlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerGamemodeChange(PlayerGameModeChangeEvent e) {
        plugin.getServer().getScheduler().runTask(plugin, () -> reloadPlayer(e.getPlayer()));
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!plugin.getJumpManager().isActive(player)) {
            return;
        }

        plugin.getJumpManager().getJumpsFull().remove(player.getUniqueId());

        int jumpsLeft = plugin.getJumpManager().getJumps().getOrDefault(uuid, 0);

        if (jumpsLeft >= 2 && !plugin.getJumpManager().getCooldown().contains(uuid)) {
            e.setCancelled(true);

            if (--jumpsLeft <= 1) {
                player.setAllowFlight(false);
                player.setFlying(false);

                int c = plugin.getConfig().getInt("Cooldown");
                if (c > 0) {
                    plugin.getJumpManager().getCooldown().add(uuid);
                    plugin.getServer().getScheduler().runTaskLater(plugin, () -> plugin.getJumpManager().getCooldown().remove(uuid), c);
                }
            } else if (player.isFlying()) {
                player.setFlying(false);
            }

            plugin.getJumpManager().getJumps().put(uuid, jumpsLeft);

            double velocity = plugin.getConfig().getDouble("Velocity");
            double velocityUp = plugin.getConfig().getDouble("VelcocityUp");

            player.setVelocity(player.getLocation().getDirection().multiply(velocity).setY(velocityUp));

            if (plugin.getConfig().getBoolean("Sound.Enable")) {
                player.getWorld().playSound(player.getLocation(), Sound.valueOf(plugin.getConfig().getString("Sound.Sound")),
                        (float) plugin.getConfig().getDouble("Sound.Volume"),
                        (float) plugin.getConfig().getDouble("Sound.Pitch"));
            }

            if (plugin.getConfig().getBoolean("Particles.Enable")) {
                String particle = plugin.getConfig().getString("Particles.Particle");
                int particleCount = plugin.getConfig().getInt("Particles.Amount");

                ParticleUtils.spawnParticles(player, particle, player.getLocation(), particleCount);
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

    private void handleJoin(Player player) {
        if (plugin.getConfig().getBoolean("EnableOnJoin")) {
            verifyEnable(player);
        }
    }

    private void verifyEnable(Player player) {
        if (player.hasPermission("infinitejump.use")) {
            plugin.getJumpManager().enable(player);
        }
    }

    private void reloadPlayer(Player player) {
        if (plugin.getJumpManager().getEnabledPlayers().contains(player.getUniqueId())) {
            plugin.getJumpManager().disableAuto(player);
            verifyEnable(player);
        }
    }
}
