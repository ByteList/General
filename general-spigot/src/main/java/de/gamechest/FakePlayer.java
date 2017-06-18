package de.gamechest;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_9_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

public class FakePlayer {
    public Location loc;
    public GameProfile profile;
    public Integer id;
    public Player p;

    public EntityPlayer entityPlayer;

    public String displayname;

    public FakePlayer(Player p, Location loc, String displayname) {
        this.p = p;
        this.loc = loc;
        this.displayname = displayname;
        this.profile = new GameProfile(UUID.randomUUID(), displayname);
        this.id = (int) Math.ceil(Math.random() * 10000) + 20000;
        CraftPlayer craftPlayer = (CraftPlayer) p;
        this.entityPlayer = new EntityPlayer(craftPlayer.getHandle().server, craftPlayer.getHandle().x(), profile, new PlayerInteractManager(craftPlayer.getHandle().getWorld()));
    }

    public void teleport(Location location) {
        PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport();
        try {
            set(packet, "e", getFixRotation(location.getYaw()));
            set(packet, "a", id);
            set(packet, "b", location.getX());
            set(packet, "c", location.getY());
            set(packet, "d", location.getZ());
            set(packet, "f", getFixRotation(location.getPitch()));
            sendPacket(packet);
            this.loc = location.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void spawn() {
        try {
            PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn();

            set(packet, "a", id);
            set(packet, "b", profile.getId());
            set(packet, "c", loc.getX());
            set(packet, "d", loc.getY());
            set(packet, "e", loc.getZ());
            set(packet, "f", getFixRotation(loc.getYaw()));
            set(packet, "g", getFixRotation(loc.getPitch()));
            DataWatcher watcher = new DataWatcher(null);
            /* in 1.8 = w.a(6, 18F);
			 * in 1.9 = w.set(DataWatcherRegistry.c.a(6),18F);
			 */
            watcher.register(new DataWatcherObject<>(6, DataWatcherRegistry.c), 3F);
            watcher.register(new DataWatcherObject<>(13, DataWatcherRegistry.a), (byte) 127);
            set(packet, "h", watcher);
//            set(reflector, "i", watcher);
            addToTabList();
            sendPacket(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(id);
        rmvFromTabList();
        sendPacket(packet);
    }

    public void addToTabList() {
        try {
            PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer);
//            PacketPlayOutPlayerInfo.PlayerInfoData data = packet.new PlayerInfoData(profile, 0, WorldSettings.EnumGamemode.SURVIVAL, CraftChatMessage.fromString(profile.getName())[0]);
//
//            @SuppressWarnings("unchecked")
//            List<PacketPlayOutPlayerInfo.PlayerInfoData> players = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) get(packet, "b");
//            players.add(data);
//
//            set(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
//            set(packet, "b", players);


            sendPacket(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void rmvFromTabList() {
        try {
            PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer);
//            packet.getClass();
//            PacketPlayOutPlayerInfo.PlayerInfoData data = packet.new PlayerInfoData(profile, 0, WorldSettings.EnumGamemode.SURVIVAL, CraftChatMessage.fromString(profile.getName())[0]);
//            @SuppressWarnings("unchecked")
//            List<PacketPlayOutPlayerInfo.PlayerInfoData> players = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) get(packet, "b");
//            players.add(data);
//
//            set(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
//            set(packet, "b", players);

            sendPacket(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeSkin(UUID uuid) {
        if (uuid == null)
            return;
        try {

            Skin skin = new Skin(uuid);

            String value = skin.getSkinValue();
            String signature = skin.getSkinSignature();
            profile.getProperties().put("textures", new Property("textures", value, signature));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeSkin(String value, String signature) {
        try {
            profile.getProperties().put("textures", new Property("textures", value, signature));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void lookAtPlayer() {
        headRotation(lookAt(loc, p.getLocation()));
    }

    public Location lookAt(Location loc, Location lookat) {
        //Clone the loc to prevent applied changes to the input loc
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
            loc.setYaw((float) loc.getYaw() - (float) Math.atan(dz / dx));
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
            PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment();
            set(packet, "a", id);
            set(packet, "b", slot);
            set(packet, "c", istack);
            sendPacket(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void animation(int animation) {
        try {
            PacketPlayOutAnimation packet = new PacketPlayOutAnimation();
            set(packet, "a", id);
            set(packet, "b", (byte) animation);
            sendPacket(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void headRotation(Location loc) {
        try {
            PacketPlayOutEntity.PacketPlayOutEntityLook packet = new PacketPlayOutEntity.PacketPlayOutEntityLook(id, getFixRotation(loc.getYaw()), getFixRotation(loc.getPitch()), true);
            PacketPlayOutEntityHeadRotation packetHead = new PacketPlayOutEntityHeadRotation();
            set(packetHead, "a", id);
            set(packetHead, "b", getFixRotation(loc.getYaw()));

            sendPacket(packet);
            sendPacket(packetHead);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sendPacket(Object packet) {
        String path = Bukkit.getServer().getClass().getPackage().getName();
        String version = path.substring(path.lastIndexOf(".") + 1, path.length());
        try {
            Method getHandle = this.p.getClass().getMethod("getHandle", new Class[0]);
            Object entityPlayer = getHandle.invoke(this.p, new Object[0]);
            Object pConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
            Class<?> packetClass = Class.forName("net.minecraft.server." + version + ".Packet");
            Method sendMethod = pConnection.getClass().getMethod("sendPacket", new Class[]{packetClass});
            sendMethod.invoke(pConnection, new Object[]{packet});
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

    public int getFixLocation(double pos) {
        return (int) MathHelper.floor(pos * 32.0D);
    }


    public byte getFixRotation(float yawpitch) {
        return (byte) ((int) (yawpitch * 256.0F / 360.0F));
    }
}
