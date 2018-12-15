package fr.mrmicky.infinitejump;

import fr.mrmicky.infinitejump.anticheathooks.AACHook;
import fr.mrmicky.infinitejump.anticheathooks.NoCheatPlusHook;
import fr.mrmicky.infinitejump.anticheathooks.SpartanHook;
import fr.mrmicky.infinitejump.anticheathooks.WatchCatHook;
import fr.mrmicky.infinitejump.utils.Particle18;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * @author MrMicky
 */
public class InfiniteJump extends JavaPlugin {

    private JumpManager jumpManager;
    private boolean is18 = false;

    @Override
    public void onEnable() {
        is18 = getServer().getVersion().contains("1.8");

        saveDefaultConfig();
        verifyConfig();

        jumpManager = new JumpManager(this);

        getCommand("infinitejump").setExecutor(new CommandInfiniteJump(this));
        getServer().getPluginManager().registerEvents(new JumpListener(this), this);

        // Anticheat hooks
        if (getServer().getPluginManager().getPlugin("Spartan") != null) {
            new SpartanHook(this);
        }

        if (getServer().getPluginManager().getPlugin("AAC") != null) {
            new AACHook(this);
        }

        if (getServer().getPluginManager().getPlugin("NoCheatPlus") != null) {
            new NoCheatPlusHook(this);
        }

        if (getServer().getPluginManager().getPlugin("WatchCat") != null) {
            new WatchCatHook(this);
        }

        if (getConfig().getBoolean("UpdateChecker")) {
            getServer().getScheduler().runTaskAsynchronously(this, this::checkUpdate);
        }

        getLogger().info("The plugin has been successfully activated");
    }

    @Override
    public void onDisable() {
        getServer().getOnlinePlayers().forEach(jumpManager::disable);
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        verifyConfig();
    }

    public JumpManager getJumpManager() {
        return jumpManager;
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
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                String lastVersion = reader.readLine();
                if (!getDescription().getVersion().equalsIgnoreCase(lastVersion)) {
                    getLogger().warning("A new version is available ! Last version is " + lastVersion + " and you are on " + getDescription().getVersion());
                    getLogger().warning("You can download it on: " + getDescription().getWebsite());
                }
            }
        } catch (IOException e) {
            // Don't display an error
        }
    }

    public void spawnParticles(Player sender, Location loc, String particleName, int amount) {
        if (is18) {
            Particle18.spawnParticles(sender, loc, particleName, amount);
            return;
        }

        Particle particle = Particle.valueOf(particleName);
        for (Player p : getServer().getOnlinePlayers()) {
            if (p.getWorld() == loc.getWorld() && loc.distanceSquared(p.getLocation()) <= 65536 && (sender == null || p.canSee(sender))) {
                p.spawnParticle(particle, loc, amount);
            }
        }
    }
}
