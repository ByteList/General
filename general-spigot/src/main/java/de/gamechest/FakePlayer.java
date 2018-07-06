package de.gamechest;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import net.minecraft.server.v1_9_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R2.CraftServer;
import org.bukkit.craftbukkit.v1_9_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * New fake player api: <code>de.gamechest.fakeplayer.FakePlayerManager</code>
 */
@Deprecated
public class FakePlayer {

    @Getter
    private final Player player;
    @Getter
    private final EntityPlayer entityFakePlayer;
    @Getter
    private final GameProfile profile;
    @Getter
    private final int entityId;
    @Getter
    private String displayname;
    @Getter
    private Location location;

    public FakePlayer(Player player, Location location, String displayname) {
        this.player = player;
        this.location = location.clone();
        this.displayname = displayname;
        this.profile = new GameProfile(UUID.randomUUID(), displayname);
        this.entityFakePlayer = new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(),
                ((CraftWorld) this.location.getWorld()).getHandle(), profile, new PlayerInteractManager(((CraftWorld) getLocation().getWorld()).getHandle()));
        this.entityId = entityFakePlayer.getId();
    }

    public void spawn() {
        try {
            entityFakePlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
            PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn(this.entityFakePlayer);

            DataWatcher watcher = new DataWatcher(null);
            watcher.register(new DataWatcherObject<>(12, DataWatcherRegistry.a), (byte) 0xFF);
            set(packet, "h", watcher);
            addToTabList();
            sendPacket(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entityFakePlayer.getId());
        rmvFromTabList();
        sendPacket(packet);
    }

    public void addToTabList() {
        try {
            PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityFakePlayer);
            sendPacket(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void rmvFromTabList() {
        try {
            PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityFakePlayer);
            sendPacket(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeSkin(UUID uuid) {
        if (uuid == null) return;

        Skin skin = new Skin(uuid);
        changeSkin(skin.getSkinValue(), skin.getSkinSignature());
    }

    public void changeSkin(String value, String signature) {
        try {
            profile.getProperties().put("textures", new Property("textures", value, signature));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void teleport(Location location) {
        try {
            entityFakePlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
            sendPacket(new PacketPlayOutEntityTeleport(entityFakePlayer));
            this.location = location.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void lookAtPlayer() {
        headRotation(getLookAtLocation(location, player.getLocation()));
    }

    public Location getLookAtLocation(Location loc, Location lookat) {
        //Clone the location to prevent applied changes to the input location
        loc = loc.clone();

        // Values of change in distance (make it relative)
        double dx = lookat.getX() - loc.getX();
        double dy = lookat.getY() - loc.getY();
        double dz = lookat.getZ() - loc.getZ();

        // Set yaw
        if (dx != 0) {
            // Set yaw start value based on dx
            if (dx < 0) {
                loc.setYaw((float) (1.5 * Math.PI));
            } else {
                loc.setYaw((float) (0.5 * Math.PI));
            }
            loc.setYaw(loc.getYaw() - (float) Math.atan(dz / dx));
        } else if (dz < 0) {
            loc.setYaw((float) Math.PI);
        }

        // Get the distance from dx/dz
        double dxz = Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));

        // Set pitch
        loc.setPitch((float) -Math.atan(dy / dxz));

        // Set values, convert to degrees (invert the yaw since Bukkit uses a different yaw dimension format)
        loc.setYaw(-loc.getYaw() * 180f / (float) Math.PI);
        loc.setPitch(loc.getPitch() * 180f / (float) Math.PI);

        return loc;
    }

    public void equip(EnumItemSlot slot, ItemStack istack) {
        try {
            sendPacket(new PacketPlayOutEntityEquipment(entityFakePlayer.getId(), slot, istack));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void animation(int animation) {
        try {
            sendPacket(new PacketPlayOutAnimation(entityFakePlayer, animation));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void headRotation(Location loc) {
        try {
            sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(entityFakePlayer.getId(), getFixRotation(loc.getYaw()), getFixRotation(loc.getPitch()), true));
            sendPacket(new PacketPlayOutEntityHeadRotation(entityFakePlayer, getFixRotation(loc.getYaw())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendPacket(Object packet) {
        String path = Bukkit.getServer().getClass().getPackage().getName();
        String version = path.substring(path.lastIndexOf(".") + 1, path.length());
        try {
            Method getHandle = ((CraftPlayer) this.player).getClass().getMethod("getHandle");
            Object entityPlayer = getHandle.invoke(this.player);
            Object pConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
            Class<?> packetClass = Class.forName("net.minecraft.server." + version + ".Packet");
            Method sendMethod = pConnection.getClass().getMethod("sendPacket", packetClass);
            sendMethod.invoke(pConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object get(Object instance, String field) throws Exception {
        Field f = instance.getClass().getDeclaredField(field);
        f.setAccessible(true);
        return f.get(instance);
    }

    private void set(Object instance, String name, Object value) throws Exception {
        Field field = instance.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(instance, value);
    }

    private int getFixLocation(double pos) {
        return MathHelper.floor(pos * 32.0D);
    }

    private byte getFixRotation(float yawpitch) {
        return (byte) ((int) (yawpitch * 256.0F / 360.0F));
    }
}
