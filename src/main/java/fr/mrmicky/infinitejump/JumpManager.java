package fr.mrmicky.infinitejump;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author MrMicky
 */
public class JumpManager extends BukkitRunnable {

    // players with ijump activated, event if they can't use it (non whitelist word or in creative)
    private final Set<UUID> enabledPlayers = new HashSet<>();

    // players that can use ijump, with the jump left
    private final Map<UUID, Integer> jumps = new HashMap<>();

    // players with a cooldown
    private final Set<UUID> cooldown = new HashSet<>();

    // players with the max amount of jumps in the jumps map, prevent lag with too many permissions check
    private final Set<UUID> jumpsFull = new HashSet<>();

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

    public int getMaxJump(Player p) {
        if (p.hasPermission("infinitejump.infinite")) {
            return 100;
        }

        for (int i = 10; i >= 3; i--) {
            if (p.hasPermission("infinitejump." + i)) {
                return i;
            }
        }
        return 2;
    }

    public void enable(Player p) {
        enabledPlayers.add(p.getUniqueId());

        if (shouldActive(p)) {
            p.setAllowFlight(true);
            p.setFlying(false);
            jumps.put(p.getUniqueId(), getMaxJump(p));
            jumpsFull.add(p.getUniqueId());
        }
    }

    public void disable(Player p) {
        enabledPlayers.remove(p.getUniqueId());
        jumpsFull.remove(p.getUniqueId());

        if (jumps.containsKey(p.getUniqueId())) {
            jumps.remove(p.getUniqueId());
            if (p.getGameMode() == GameMode.ADVENTURE || p.getGameMode() == GameMode.SURVIVAL) {
                p.setAllowFlight(false);
            }
        }
    }

    public boolean isActive(Player p) {
        return jumps.containsKey(p.getUniqueId());
    }

    public boolean shouldActive(Player p) {
        return (p.getGameMode() == GameMode.ADVENTURE || p.getGameMode() == GameMode.SURVIVAL) && isWhitelistWorld(p.getWorld());
    }

    public boolean isWhitelistWorld(World w) {
        if (!plugin.getConfig().getBoolean("WorldsWhitelist")) {
            return true;
        }

        for (String s : plugin.getConfig().getStringList("Worlds")) {
            if (s.equalsIgnoreCase(w.getName())) {
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
