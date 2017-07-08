package de.gamechest.nick;

import com.mojang.authlib.GameProfile;
import de.gamechest.reflector.Reflection;
import net.minecraft.server.v1_9_R2.PacketPlayOutEntityDestroy;
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

    protected NickPackets(Nick nick) {
        this.nick = nick;
    }

    public void nickPlayer(Player p, String nickname) throws Exception {
        CraftPlayer cp = ((CraftPlayer) p);

        GameProfile gp = cp.getProfile();
        gp.getProperties().clear();

        List<Player> players = Bukkit.getOnlinePlayers().stream().filter(player -> player.canSee(p)).collect(Collectors.toList());
        players.remove(p);

        players.forEach(player -> player.hidePlayer(p));

        getField(GameProfile.class, "name").set(cp.getProfile(), nickname);
        nick.setSkin(p.getUniqueId(), nickname);

        PacketPlayOutPlayerInfo remove = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, cp.getHandle());
        Reflection.sendListPacket(players, remove);
        Reflection.sendPlayerPacket(p, remove);

        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(cp.getEntityId());
        Reflection.sendListPacket(players, destroy);

        PacketPlayOutPlayerInfo add = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, cp.getHandle());
        Reflection.sendListPacket(players, add);
        Reflection.sendPlayerPacket(p, add);

        nick.performDeath(p);

        PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(cp.getHandle());
        Reflection.sendListPacket(players, spawn);


        players.forEach(player -> player.showPlayer(p));

        System.out.println("[GCG/Nick] Player " + p.getCustomName() + " is now nicked as " + nickname);
    }


    public void unnickPlayer(Player p) throws Exception {
        CraftPlayer cp = (CraftPlayer) p;

        GameProfile gp = cp.getProfile();
        gp.getProperties().clear();

        List<Player> players = Bukkit.getOnlinePlayers().stream().filter(player -> player.canSee(p)).collect(Collectors.toList());
        players.remove(p);

        players.forEach(player -> player.hidePlayer(p));

        getField(GameProfile.class, "name").set(cp.getProfile(), p.getCustomName());
        nick.resetSkin(p.getUniqueId());

        PacketPlayOutPlayerInfo remove = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, cp.getHandle());
        Reflection.sendListPacket(players, remove);
        Reflection.sendPlayerPacket(p, remove);

        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(cp.getEntityId());
        Reflection.sendListPacket(players, destroy);

        PacketPlayOutPlayerInfo add = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, cp.getHandle());
        Reflection.sendListPacket(players, add);
        Reflection.sendPlayerPacket(p, add);

        nick.performDeath(p);

        PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(cp.getHandle());
        Reflection.sendListPacket(players, spawn);

        players.forEach(player -> player.showPlayer(p));


        System.out.println("[GCG/Nick] Player " + p.getName() + " is now unnicked");
    }

    private Field getField(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException | SecurityException ignored) {
        }
        return null;
    }

}
