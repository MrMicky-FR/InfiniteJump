package fr.mrmicky.infinitejump;

import fr.mrmicky.infinitejump.anticheathooks.AACHook;
import fr.mrmicky.infinitejump.anticheathooks.NoCheatPlusHook;
import fr.mrmicky.infinitejump.anticheathooks.SpartanHook;
import fr.mrmicky.infinitejump.particle.ParticleUtils;
import org.bukkit.Sound;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public final class InfiniteJump extends JavaPlugin {

    private JumpManager jumpManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        verifyConfig();

        jumpManager = new JumpManager(this);

        getCommand("infinitejump").setExecutor(new CommandInfiniteJump(this));
        getServer().getPluginManager().registerEvents(new JumpListener(this), this);

        // AntiCheat hooks
        if (getServer().getPluginManager().getPlugin("Spartan") != null) {
            new SpartanHook(this);
        }

        if (getServer().getPluginManager().getPlugin("AAC") != null) {
            new AACHook(this);
        }

        if (getServer().getPluginManager().getPlugin("NoCheatPlus") != null) {
            new NoCheatPlusHook(this);
        }

        if (getConfig().getBoolean("UpdateChecker")) {
            getServer().getScheduler().runTaskAsynchronously(this, this::checkUpdate);
        }
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
            getConfig().set("Sound.Sound", getServer().getVersion().contains("1.8") ? "BAT_TAKEOFF" : "ENTITY_BAT_TAKEOFF");
        }

        String particle = getConfig().getString("Particles.Particle");

        if (!ParticleUtils.isValidParticle(particle)) {
            getLogger().warning("Wrong particle type in the config: " + particle);
            getConfig().set("Particles.Particle", "CRIT_MAGIC");
        }
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
            // ignore
        }
    }
}
