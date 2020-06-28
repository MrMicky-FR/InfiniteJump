package fr.mrmicky.infinitejump.particle;

import fr.mrmicky.infinitejump.utils.FastReflection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public final class ParticleUtils {

    private ParticleUtils() {
        throw new UnsupportedOperationException();
    }

    private static final boolean LEGACY = !FastReflection.optionalClass("org.bukkit.Particle").isPresent();

    public static void spawnParticles(Player sender, String particleName, Location loc, int count) {
        if (LEGACY) {
            LegacyParticles.spawnParticles(sender, particleName, loc, count);
            return;
        }

        Particle particle = Particle.valueOf(particleName);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getWorld() == loc.getWorld() && loc.distanceSquared(p.getLocation()) <= 65536 && (sender == null || p.canSee(sender))) {
                p.spawnParticle(particle, loc, count);
            }
        }
    }

    public static boolean isValidParticle(String particleName) {
        try {
            if (LEGACY) {
                LegacyParticles.getParticle(particleName);
            } else {
                Particle.valueOf(particleName);
            }
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
