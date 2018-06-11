package fr.mrmicky.infinitejump;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author MrMicky
 */
public class Particle18 {

    private static final String PACKAGE_NAME;

    private static final Constructor<?> PACKET_PARTICLE;
    private static final Class<?> ENUM_PARTICLE;

    private static final Field PLAYER_CONNECTION;
    private static final Method SEND_PACKET;

    static {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        String ver = name.substring(name.lastIndexOf('.') + 1);

        PACKAGE_NAME = "net.minecraft.server." + ver;

        Constructor<?> packetParticle = null;
        Class<?> enumParticle = null;

        Field playerConnection = null;
        Method sendPacket = null;

        try {
            Class<?> packetParticleClass = getClass("PacketPlayOutWorldParticles");
            enumParticle = getClass("EnumParticle");

            Class<?> playerClass = getClass("EntityPlayer");
            Class<?> playerConnectionClass = getClass("PlayerConnection");
            Class<?> packetClass = getClass("Packet");

            packetParticle = packetParticleClass.getConstructor(enumParticle, boolean.class, float.class, float.class,
                    float.class, float.class, float.class, float.class, float.class, int.class, int[].class);

            playerConnection = playerClass.getField("playerConnection");
            sendPacket = playerConnectionClass.getMethod("sendPacket", packetClass);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        PACKET_PARTICLE = packetParticle;
        ENUM_PARTICLE = enumParticle;

        PLAYER_CONNECTION = playerConnection;
        SEND_PACKET = sendPacket;
    }

    private static Class<?> getClass(String name) throws ClassNotFoundException {
        return Class.forName(PACKAGE_NAME + "." + name);
    }

    public static void displayParticle(Location loc, String particle, int count) {
        try {
            Object packet = PACKET_PARTICLE.newInstance(getEnum(ENUM_PARTICLE.getName() + "." + particle.toUpperCase()),
                    true, (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), 0.0F, 0.0F, 0.0F, 1.0F, count,
                    new int[]{});
            sendPacket(packet, loc.getWorld());
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Enum<?> getEnum(String enumFullName) throws ClassNotFoundException {
        String[] x = enumFullName.split("\\.(?=[^\\.]+$)");
        if (x.length == 2) {
            String enumClassName = x[0];
            String enumName = x[1];
            Class<Enum> cl = (Class<Enum>) Class.forName(enumClassName);
            return Enum.valueOf(cl, enumName);
        }
        return null;
    }

    private static void sendPacket(Object packet, World w) throws ReflectiveOperationException {
        for (Player p : w.getPlayers()) {
            Object handle = p.getClass().getDeclaredMethod("getHandle").invoke(p);
            Object playerConnection = PLAYER_CONNECTION.get(handle);
            SEND_PACKET.invoke(playerConnection, packet);
        }
    }

    public static boolean isValidParticle(String particle) {
        try {
            getEnum(ENUM_PARTICLE.getName() + "." + particle);
            return true;
        } catch (IllegalArgumentException | ClassNotFoundException e) {
            return false;
        }
    }
}
