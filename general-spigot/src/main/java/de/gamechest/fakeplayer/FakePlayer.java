package de.gamechest.fakeplayer;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_9_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R2.CraftServer;
import org.bukkit.craftbukkit.v1_9_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Created by ByteList on 02.07.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
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

    @Getter
    private boolean spawned, sneaking, sprinting, onFire;
    @Getter@Setter
    private int fireTicks;
    @Getter@Setter
    private BukkitTask fireTask;
    @Getter@Setter
    private boolean interacted;

    private byte bitMaskStatus = 0;

    @Getter
    private final FakePlayerRunnable runnable;

    private FakePlayer(Player player, Location location, String displayname, FakePlayerRunnable runnable) {
        this.player = player;
        this.location = location.clone();
        this.displayname = displayname;
        this.runnable = runnable;
        this.profile = new GameProfile(UUID.randomUUID(), displayname);
        this.entityFakePlayer = new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(),
                ((CraftWorld) this.location.getWorld()).getHandle(), profile, new PlayerInteractManager(((CraftWorld) getLocation().getWorld()).getHandle()));
        this.entityId = entityFakePlayer.getId();
    }

    private FakePlayer(Player player, Location location, String displayname, FakePlayerRunnable runnable, String skinValue, String skinSignature) {
        this.player = player;
        this.location = location.clone();
        this.displayname = displayname;
        this.runnable = runnable;
        this.profile = new GameProfile(UUID.randomUUID(), displayname);
        changeSkin(skinValue, skinSignature);
        this.entityFakePlayer = new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(),
                ((CraftWorld) this.location.getWorld()).getHandle(), profile, new PlayerInteractManager(((CraftWorld) getLocation().getWorld()).getHandle()));
        this.entityId = entityFakePlayer.getId();
    }

    public void spawn() {
        if(this.spawned) return;

        entityFakePlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
        PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn(this.entityFakePlayer);

        DataWatcher watcher = new DataWatcher(null);
        watcher.register(new DataWatcherObject<>(0, DataWatcherRegistry.a), bitMaskStatus);
        watcher.register(new DataWatcherObject<>(12, DataWatcherRegistry.a), (byte) 0xFF);
        set(packet, "h", watcher);
        addToTabList();
        sendPacket(packet);
        this.spawned = true;
    }

    public void destroy() {
        if(!this.spawned) return;

        removeFromTabList();
        sendPacket(new PacketPlayOutEntityDestroy(entityFakePlayer.getId()));
        this.spawned = false;
    }

    public void addToTabList() {
        sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityFakePlayer));
    }

    public void removeFromTabList() {
        sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityFakePlayer));
    }

    public void changeSkin(UUID uuid) {
        if (uuid == null) return;

        Skin skin = new Skin(uuid);
        changeSkin(skin.getSkinValue(), skin.getSkinSignature());
    }

    public void changeSkin(String value, String signature) {
        profile.getProperties().put("textures", new Property("textures", value, signature));
    }

    public void teleport(Location location) {
        if(!this.spawned) return;

        entityFakePlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
        sendPacket(new PacketPlayOutEntityTeleport(entityFakePlayer));
        this.location = location.clone();
    }

    public void lookAtPlayer() {
        if(!this.spawned) return;

        lookAtLocation(player.getLocation());
    }

    public void lookAtLocation(Location lookAt) {
        //Clone the location to prevent applied changes to the input location
        Location location = this.location.clone();

        // Values of change in distance (make it relative)
        double dx = lookAt.getX() - location.getX();
        double dy = lookAt.getY() - location.getY();
        double dz = lookAt.getZ() - location.getZ();

        // Set yaw
        if (dx != 0) {
            // Set yaw start value based on dx
            if (dx < 0) {
                location.setYaw((float) (1.5 * Math.PI));
            } else {
                location.setYaw((float) (0.5 * Math.PI));
            }
            location.setYaw(location.getYaw() - (float) Math.atan(dz / dx));
        } else if (dz < 0) {
            location.setYaw((float) Math.PI);
        }

        // Get the distance from dx/dz
        double dxz = Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));

        // Set pitch
        location.setPitch((float) -Math.atan(dy / dxz));

        // Set values, convert to degrees (invert the yaw since Bukkit uses a different yaw dimension format)
        location.setYaw(-location.getYaw() * 180f / (float) Math.PI);
        location.setPitch(location.getPitch() * 180f / (float) Math.PI);
    }

    public void equip(EnumItemSlot slot, ItemStack istack) {
        sendPacket(new PacketPlayOutEntityEquipment(entityFakePlayer.getId(), slot, istack));
    }

    public void animation(int animation) {
        if(!this.spawned) return;

        sendPacket(new PacketPlayOutAnimation(entityFakePlayer, animation));
    }

    public void status(int status) {
        if(!this.spawned) return;

        sendPacket(new PacketPlayOutEntityStatus(entityFakePlayer, (byte) status));
    }

    public void sneak(boolean state) {
        updateMetadata(1, state);
        this.sneaking = state;
    }

    public void sprint(boolean state) {
        updateMetadata(3, state);
        this.sprinting = state;
    }

    public void fire(boolean state) {
        updateMetadata(0, state);
        this.onFire = state;
    }

    public void updateMetadata(int bit, boolean state) {
        if(!this.spawned) return;

        DataWatcher dataWatcher = new DataWatcher(entityFakePlayer);
        dataWatcher.register(new DataWatcherObject<>(0, DataWatcherRegistry.a), changeMask(bit, state));

        sendPacket(new PacketPlayOutEntityMetadata(entityId, dataWatcher, true));
    }


    public void headRotation(Location loc) {
        sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(entityFakePlayer.getId(), getFixRotation(loc.getYaw()), getFixRotation(loc.getPitch()), true));
        sendPacket(new PacketPlayOutEntityHeadRotation(entityFakePlayer, getFixRotation(loc.getYaw())));
    }

    public void sendPacket(Object packet) {
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

    private byte changeMask(int bit, boolean state) {
        if (state) return (byte) (bitMaskStatus | 1 << bit);
        else return (byte) (bitMaskStatus & ~(1 << bit));
    }

    private Object get(Object instance, String field) throws IllegalAccessException, NoSuchFieldException {
        Field f = instance.getClass().getDeclaredField(field);
        f.setAccessible(true);
        return f.get(instance);
    }

    private void set(Object instance, String name, Object value) {
        try {
            Field field = instance.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(instance, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private int getFixLocation(double pos) {
        return MathHelper.floor(pos * 32.0D);
    }

    private byte getFixRotation(float yawpitch) {
        return (byte) ((int) (yawpitch * 256.0F / 360.0F));
    }

    public static class Skin {
        private UUID uuid;
        private String name;
        private String value;
        private String signature;

        Skin(UUID uuid) {
            this.uuid = uuid;
            load();
        }

        private void load() {
            try {
                URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + this.uuid.toString().replace("-", "") + "?unsigned=false");
                URLConnection uc = url.openConnection();
                uc.setUseCaches(false);
                uc.setDefaultUseCaches(false);
                uc.addRequestProperty("User-Agent", "Mozilla/5.0");
                uc.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
                uc.addRequestProperty("Pragma", "no-cache");

                Scanner scanner = new Scanner(uc.getInputStream(), "UTF-8").useDelimiter("\\A");

                String json = scanner.next();
                scanner.close();
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(json);
                JSONArray properties = (JSONArray) ((JSONObject) obj).get("properties");
                for (int i = 0; i < properties.size(); i++) {
                    try {
                        JSONObject property = (JSONObject) properties.get(i);
                        String name = (String) property.get("name");
                        String value = (String) property.get("value");
                        String signature = property.containsKey("signature") ? (String) property.get("signature") : null;

                        this.name = name;
                        this.value = value;
                        this.signature = signature;
                    } catch (Exception e) {
                        Bukkit.getLogger().log(Level.WARNING, "Failed to apply auth property", e);
                    }
                }
            } catch (Exception ignored) {
            }
        }

        public String getSkinValue() {
            return this.value;
        }

        public String getSkinName() {
            return this.name;
        }

        public String getSkinSignature() {
            return this.signature;
        }
    }

    public static class FakePlayerBuilder {

        private String displayname, skinValue, skinSignature;
        private Player player;
        private Location location;
        private FakePlayerRunnable runnable;

        public static FakePlayerBuilder newInstance() {
            return new FakePlayerBuilder();
        }

        public FakePlayerBuilder displayname(String displayname) {
            this.displayname = displayname;
            return this;
        }

        public FakePlayerBuilder player(Player player) {
            this.player = player;
            return this;
        }

        public FakePlayerBuilder location(Location location) {
            this.location = location;
            return this;
        }

        public FakePlayerBuilder runnable(FakePlayerRunnable runnable) {
            this.runnable = runnable;
            return this;
        }

        public FakePlayerBuilder skin(String skinValue, String skinSignature) {
            this.skinValue = skinValue;
            this.skinSignature = skinSignature;
            return this;
        }

        public FakePlayerBuilder skin(UUID uuid) {
            Skin skin = new Skin(uuid);

            this.skinValue = skin.getSkinValue();
            this.skinSignature = skin.getSkinSignature();
            return this;
        }

        public FakePlayer build() {
            FakePlayer fakePlayer;
            if(this.skinValue != null && this.skinSignature != null) {
                fakePlayer = new FakePlayer(this.player, this.location, this.displayname, this.runnable, this.skinValue, this.skinSignature);
            } else {
                fakePlayer = new FakePlayer(this.player, this.location, this.displayname, this.runnable);
            }
            return fakePlayer;
        }
    }
}
