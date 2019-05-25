package de.gamechest.nick;

import com.mojang.authlib.GameProfile;
import de.gamechest.GameChest;
import de.gamechest.reflector.Reflection;
import net.minecraft.server.v1_9_R2.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_9_R2.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_9_R2.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_9_R2.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

class NickPackets {

    private final Nick nick;

    NickPackets(Nick nick) {
        this.nick = nick;
    }

    void nickPlayer(Player p, String nickname, boolean onJoin) throws Exception {
        CraftPlayer cp = ((CraftPlayer) p);

        GameProfile gp = cp.getProfile();
        gp.getProperties().clear();

        List<Player> players = Bukkit.getOnlinePlayers().stream().filter(player -> player.canSee(p)).collect(Collectors.toList());
        players.remove(p);

        players.forEach(player -> {
            player.hidePlayer(p);
            p.hidePlayer(player);
        });

        getNameField(GameProfile.class).set(cp.getProfile(), nickname);
        nick.setSkin(p.getUniqueId(), nickname);

        if(!onJoin) {
            PacketPlayOutPlayerInfo remove = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, cp.getHandle());
            Reflection.sendListPacket(players, remove);
            Reflection.sendPlayerPacket(p, remove);

            PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(cp.getEntityId());
            Reflection.sendListPacket(players, destroy);

            PacketPlayOutPlayerInfo add = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, cp.getHandle());
            Reflection.sendListPacket(players, add);
            Reflection.sendPlayerPacket(p, add);

            nick.updateSkin(p);
            PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(cp.getHandle());
            Reflection.sendListPacket(players, spawn);

            PacketPlayOutEntityTeleport teleport = new PacketPlayOutEntityTeleport();
            float pitch = cp.getLocation().getPitch(), yaw = cp.getLocation().getYaw();
            double x = cp.getLocation().getX(), y = cp.getLocation().getY(), z = cp.getLocation().getZ();
            double yd = -Math.sin(Math.toRadians(pitch));
            double xz = Math.cos(Math.toRadians(pitch));
            double xd = -xz * Math.sin(Math.toRadians(yaw));
            double zd = xz * Math.cos(Math.toRadians(yaw));

            Reflection.setValue(teleport, "a", cp.getHandle().getId());
            Reflection.setValue(teleport, "b", x + (xd * 2));
            Reflection.setValue(teleport, "c", y + (yd * 2));
            Reflection.setValue(teleport, "d", z + (zd * 2));
            Reflection.setValue(teleport, "g", false);

            Reflection.sendListPacket(players, teleport);
        }

        Bukkit.getScheduler().runTaskLater(GameChest.getInstance(), ()->
                players.forEach(player -> {
                    player.showPlayer(p);
                    p.showPlayer(player);
                }), 15L);

        System.out.println("[GCG/ChestNick] Player " + p.getCustomName() + " is now nicked as " + nickname);
    }


    void unnickPlayer(Player p) throws Exception {
        CraftPlayer cp = (CraftPlayer) p;

        GameProfile gp = cp.getProfile();
        gp.getProperties().clear();

        List<Player> players = Bukkit.getOnlinePlayers().stream().filter(player -> player.canSee(p)).collect(Collectors.toList());
        players.remove(p);

        players.forEach(player -> {
            player.hidePlayer(p);
            p.hidePlayer(player);
        });

        getNameField(GameProfile.class).set(cp.getProfile(), p.getCustomName());
        nick.resetSkin(p.getUniqueId());

        PacketPlayOutPlayerInfo remove = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, cp.getHandle());
        Reflection.sendListPacket(players, remove);
        Reflection.sendPlayerPacket(p, remove);

        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(cp.getEntityId());
        Reflection.sendListPacket(players, destroy);

        PacketPlayOutPlayerInfo add = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, cp.getHandle());
        Reflection.sendListPacket(players, add);
        Reflection.sendPlayerPacket(p, add);

        nick.updateSkin(p);

        PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(cp.getHandle());
        Reflection.sendListPacket(players, spawn);

        PacketPlayOutEntityTeleport teleport = new PacketPlayOutEntityTeleport();
        float pitch = cp.getLocation().getPitch(), yaw = cp.getLocation().getYaw();
        double x = cp.getLocation().getX(), y = cp.getLocation().getY(), z = cp.getLocation().getZ();
        double yd = -Math.sin(Math.toRadians(pitch));
        double xz = Math.cos(Math.toRadians(pitch));
        double xd = -xz * Math.sin(Math.toRadians(yaw));
        double zd = xz * Math.cos(Math.toRadians(yaw));

        Reflection.setValue(teleport, "a", cp.getHandle().getId());
        Reflection.setValue(teleport, "b", x + (xd * 2));
        Reflection.setValue(teleport, "c", y + (yd * 2));
        Reflection.setValue(teleport, "d", z + (zd * 2));
        Reflection.setValue(teleport, "g", false);

        Reflection.sendListPacket(players, teleport);

        Bukkit.getScheduler().runTaskLater(GameChest.getInstance(), ()->
        players.forEach(player -> {
            player.showPlayer(p);
            p.showPlayer(player);
        }), 15L);

        System.out.println("[GCG/ChestNick] Player " + p.getName() + " is now unnicked");
    }

    private Field getNameField(Class<?> clazz) {
        try {
            Field field = clazz.getDeclaredField("name");
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException | SecurityException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
