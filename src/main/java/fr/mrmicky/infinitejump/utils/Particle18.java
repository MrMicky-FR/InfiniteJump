package fr.mrmicky.infinitejump.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

/**
 * @author MrMicky
 */
public class Particle18 {

    private static final String PACKAGE_NAME;

    private static final Class<?> ENUM_PARTICLE;
    private static final Method PLAYER_GET_HANDLE;
    private static final Method WORLD_GET_HANDLE;
    private static final Method SEND_PARTICLES;

    static {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        String ver = name.substring(name.lastIndexOf('.') + 1);

        PACKAGE_NAME = "net.minecraft.server." + ver;

        Class<?> enumParticle = null;
        Method playerGetHandle = null;
        Method worldGetHandle = null;
        Method sendParticles = null;

        try {
            enumParticle = getClass("EnumParticle");

            Class<?> craftPlayerClass = Class.forName(name + ".entity.CraftPlayer");
            Class<?> craftWorldClass = Class.forName(name + ".CraftWorld");
            Class<?> worldServerClass = getClass("WorldServer");
            Class<?> entityPlayerClass = getClass("EntityPlayer");

            playerGetHandle = craftPlayerClass.getDeclaredMethod("getHandle");
            worldGetHandle = craftWorldClass.getDeclaredMethod("getHandle");

            sendParticles = worldServerClass.getDeclaredMethod("sendParticles", entityPlayerClass, enumParticle, boolean.class,
                    double.class, double.class, double.class, int.class, double.class, double.class, double.class, double.class, int[].class);

        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        ENUM_PARTICLE = enumParticle;
        PLAYER_GET_HANDLE = playerGetHandle;
        WORLD_GET_HANDLE = worldGetHandle;
        SEND_PARTICLES = sendParticles;
    }

    private static Class<?> getClass(String name) throws ClassNotFoundException {
        return Class.forName(PACKAGE_NAME + "." + name);
    }

    public static void spawnParticles(Player p, Location loc, String particle, int count) {
        try {
            Object entityPlayer = p == null ? null : PLAYER_GET_HANDLE.invoke(p);
            Object worldServer = WORLD_GET_HANDLE.invoke(loc.getWorld());
            SEND_PARTICLES.invoke(worldServer, entityPlayer, getParticle(particle), true, loc.getX(), loc.getY(), loc.getZ(), count, 0.0, 0.0, 0.0, 1.0, new int[0]);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private static Enum<?> getParticle(String particle) {
        return Enum.valueOf((Class<Enum>) ENUM_PARTICLE, particle.toUpperCase());
    }

    public static boolean isValidParticle(String particle) {
        try {
            getParticle(particle);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
