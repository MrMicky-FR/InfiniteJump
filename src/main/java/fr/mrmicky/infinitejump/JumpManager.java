package fr.mrmicky.infinitejump;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class JumpManager extends BukkitRunnable {

    // players with ijump activated, event if they can't use it (non whitelist word or in creative)
    private final Set<UUID> enabledPlayers = new HashSet<>();

    // players that can use ijump, with the jump left
    private final Map<UUID, Integer> jumps = new HashMap<>();

    // players with a cooldown
    private final Set<UUID> cooldown = new HashSet<>();

    // players with the max amount of jumps in the jumps map, prevent lag with too many permissions check
    private final Set<UUID> jumpsFull = new HashSet<>();

    // last time this player used double jump - for anti-cheats
    private final Map<UUID, Instant> lastJumpTime = new HashMap<>();

    private final InfiniteJump plugin;

    public JumpManager(InfiniteJump plugin) {
        this.plugin = plugin;

        runTaskTimer(plugin, 10, 10);
    }

    @Override
    public void run() {
        for (UUID uuid : jumps.keySet()) {
            if (!cooldown.contains(uuid) && !jumpsFull.contains(uuid)) {

                Player p = plugin.getServer().getPlayer(uuid);
                if (p != null && p.isOnGround()) {
                    p.setAllowFlight(true);
                    jumps.put(uuid, getMaxJump(p));
                    jumpsFull.add(p.getUniqueId());
                }
            }
        }
    }

    public int getMaxJump(Player player) {
        if (player.hasPermission("infinitejump.infinite")) {
            return 100;
        }

        for (int i = 10; i >= 3; i--) {
            if (player.hasPermission("infinitejump." + i)) {
                return i;
            }
        }
        return 2;
    }

    public void enable(Player player) {
        enabledPlayers.add(player.getUniqueId());

        if (shouldActive(player)) {
            player.setAllowFlight(true);
            player.setFlying(false);
            jumps.put(player.getUniqueId(), getMaxJump(player));
            jumpsFull.add(player.getUniqueId());
        }
    }

    public void disable(Player player) {
        enabledPlayers.remove(player.getUniqueId());

        disableAuto(player);
    }

    public void disableAuto(Player player) {
        jumpsFull.remove(player.getUniqueId());
        lastJumpTime.remove(player.getUniqueId());

        if (jumps.remove(player.getUniqueId()) != null) {
            if (player.getGameMode() == GameMode.ADVENTURE || player.getGameMode() == GameMode.SURVIVAL) {
                player.setAllowFlight(false);
            }
        }
    }

    public boolean hasRecentJump(Player player) {
        if (!isActive(player)) {
            return false;
        }

        Instant lastJump = lastJumpTime.get(player.getUniqueId());

        if (lastJump == null) {
            return false;
        }

        return lastJump.plusSeconds(5).isAfter(Instant.now());
    }

    public void updateLastJump(Player player) {
        lastJumpTime.put(player.getUniqueId(), Instant.now());
    }

    public boolean isActive(Player player) {
        return jumps.containsKey(player.getUniqueId());
    }

    public boolean shouldActive(Player player) {
        return (player.getGameMode() == GameMode.ADVENTURE || player.getGameMode() == GameMode.SURVIVAL) && isWhitelistWorld(player.getWorld());
    }

    public boolean isWhitelistWorld(World world) {
        if (!plugin.getConfig().getBoolean("WorldsWhitelist")) {
            return true;
        }

        for (String s : plugin.getConfig().getStringList("Worlds")) {
            if (s.equalsIgnoreCase(world.getName())) {
                return true;
            }
        }

        return false;
    }

    public Set<UUID> getEnabledPlayers() {
        return enabledPlayers;
    }

    public Map<UUID, Integer> getJumps() {
        return jumps;
    }

    public Set<UUID> getCooldown() {
        return cooldown;
    }

    public Set<UUID> getJumpsFull() {
        return jumpsFull;
    }
}
