package fr.mrmicky.infinitejump;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * @author MrMicky
 */
public class JumpManager extends BukkitRunnable {

    private InfiniteJump m;

    private List<UUID> enabledPlayers = new ArrayList<>();
    private Map<UUID, Integer> jumps = new HashMap<>();
    private List<UUID> cooldown = new ArrayList<>();

    public JumpManager(InfiniteJump m) {
        this.m = m;

        runTaskTimer(m, 10, 10);
    }

    @Override
    public void run() {
        for (Map.Entry<UUID, Integer> entry : jumps.entrySet()) {
            if (entry.getValue() > 0 && !cooldown.contains(entry.getKey())) {

                Player p = m.getServer().getPlayer(entry.getKey());
                if (p != null && entry.getValue() > 0 && p.isOnGround()) {
                    p.setAllowFlight(true);
                    jumps.put(entry.getKey(), getMaxJump(p));
                }
            }
        }
    }

    public int getMaxJump(Player p) {
        if (p.hasPermission("infinitejump.infinite")) {
            return 100;
        }

        for (int i = 10; i > 1; i--) {
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
        }
    }

    public void disable(Player p) {
        enabledPlayers.remove(p.getUniqueId());

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
        if (!m.getConfig().getBoolean("WorldsWhitelist")) {
            return true;
        }

        for (String s : m.getConfig().getStringList("Worlds")) {
            if (s.equalsIgnoreCase(w.getName())) {
                return true;
            }
        }

        return false;
    }

    public List<UUID> getEnabledPlayers() {
        return enabledPlayers;
    }

    public Map<UUID, Integer> getJumps() {
        return jumps;
    }

    public List<UUID> getCooldown() {
        return cooldown;
    }
}
