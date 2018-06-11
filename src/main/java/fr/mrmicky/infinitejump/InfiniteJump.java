package fr.mrmicky.infinitejump;

import fr.mrmicky.infinitejump.anticheathooks.AACHook;
import fr.mrmicky.infinitejump.anticheathooks.NoCheatPlusHook;
import fr.mrmicky.infinitejump.anticheathooks.SpartanHook;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * @author MrMicky
 */
public class InfiniteJump extends JavaPlugin {

    private boolean is18 = false;
    private List<UUID> jumps = new ArrayList<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        is18 = getServer().getVersion().contains("1.8");

        getServer().getPluginManager().registerEvents(new JumpListener(this), this);
        getCommand("infinitejump").setExecutor(new CommandInfiniteJump(this));

        verifyConfig();

        // Anticheat hooks
        if (getServer().getPluginManager().isPluginEnabled("Spartan")) {
            new SpartanHook(this);
        }

        if (getServer().getPluginManager().isPluginEnabled("AAC")) {
            new AACHook(this);
        }

        if (getServer().getPluginManager().isPluginEnabled("NoCheatPlus")) {
            new NoCheatPlusHook(this);
        }

        // Support reloads
        for (Player p : getServer().getOnlinePlayers()) {
            if (p.hasPermission("infinitejump.use")) {
                jumps.add(p.getUniqueId());
            }
        }

        if (getConfig().getBoolean("UpdateChecker")) {
            getServer().getScheduler().runTaskLaterAsynchronously(this, this::checkUpdate, 60);
        }

        getLogger().info("The plugin has been successfully activated");
    }

    @Override
    public void onDisable() {
        jumps.forEach(uuid -> getServer().getPlayer(uuid).setAllowFlight(false));
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        verifyConfig();
    }

    private void verifyConfig() {
        String sound = getConfig().getString("Sound.Sound");
        try {
            Sound.valueOf(sound);
        } catch (IllegalArgumentException e) {
            getLogger().warning("Wrong sound type in the config: " + sound);
            getConfig().set("Sound.Sound", is18 ? "BAT_TAKEOFF" : "ENTITY_BAT_TAKEOFF");
        }

        String particle = getConfig().getString("Particles.Particle");
        if (is18) {
            if (Particle18.isValidParticle(particle)) {
                return;
            }
        } else {
            try {
                Particle.valueOf(particle);
                return;
            } catch (IllegalArgumentException e) {
                // Wrong particle type
            }
        }

        getLogger().warning("Wrong particle type in the config: " + particle);
        getConfig().set("Particles.Particle", "CRIT_MAGIC");
    }

    private void checkUpdate() {
        try {
            URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=51522");
            String lastVersion = new BufferedReader(new InputStreamReader(url.openStream())).readLine();

            if (!getDescription().getVersion().equals(lastVersion)) {
                getLogger().info("A new version is avaible ! Last version is " + lastVersion + " and you are on "
                        + getDescription().getVersion());
                getLogger().info("You can download it on: " + getDescription().getWebsite());
            }
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Failed to check for update on SpigotMC", e);
        }
    }

    public void spawnParticle(Location loc, String particle, int amount) {
        if (is18) {
            Particle18.displayParticle(loc, particle, amount);
        } else {
            loc.getWorld().spawnParticle(Particle.valueOf(particle), loc, amount);
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

    public List<UUID> getJumps() {
        return jumps;
    }
}
