package fr.mrmicky.infinitejump.particle;

import fr.mrmicky.infinitejump.utils.FastReflection;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

/**
 * @author MrMicky
 */
final class LegacyParticles {

    private static final Class<?> ENUM_PARTICLE;
    private static final Method PLAYER_GET_HANDLE;
    private static final Method WORLD_GET_HANDLE;
    private static final Method SEND_PARTICLES;

    static {
        try {
            ENUM_PARTICLE = FastReflection.nmsClass("EnumParticle");

            Class<?> craftPlayerClass = FastReflection.obcClass("entity.CraftPlayer");
            Class<?> craftWorldClass = FastReflection.obcClass("CraftWorld");
            Class<?> worldServerClass = FastReflection.nmsClass("WorldServer");
            Class<?> entityPlayerClass = FastReflection.nmsClass("EntityPlayer");

            PLAYER_GET_HANDLE = craftPlayerClass.getDeclaredMethod("getHandle");
            WORLD_GET_HANDLE = craftWorldClass.getDeclaredMethod("getHandle");

            SEND_PARTICLES = worldServerClass.getDeclaredMethod("sendParticles", entityPlayerClass, ENUM_PARTICLE, boolean.class,
                    double.class, double.class, double.class, int.class, double.class, double.class, double.class, double.class, int[].class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private LegacyParticles() {
        throw new UnsupportedOperationException();
    }

    static void spawnParticles(Player player, String particle, Location loc, int count) {
        try {
            Object entityPlayer = player == null ? null : PLAYER_GET_HANDLE.invoke(player);
            Object worldServer = WORLD_GET_HANDLE.invoke(loc.getWorld());
            SEND_PARTICLES.invoke(worldServer, entityPlayer, getParticle(particle), true, loc.getX(), loc.getY(), loc.getZ(), count, 0.0, 0.0, 0.0, 1.0, new int[0]);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    static Object getParticle(String particle) {
        return FastReflection.enumValueOf(ENUM_PARTICLE, particle);
    }
}
