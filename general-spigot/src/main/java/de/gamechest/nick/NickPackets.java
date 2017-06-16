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
import java.util.ArrayList;
import java.util.List;

class NickPackets {

    private final Nick nick;

    protected NickPackets(Nick nick) {
        this.nick = nick;
    }

    public void nickPlayer(Player p, String nickname) throws Exception {
//        getField(GameProfile.class, "name").set(((CraftPlayer) p).getProfile(), p.getCustomName());
//        nick.setSkin(p.getUniqueId(), nickname);
//        updatePlayer(p);
        CraftPlayer cp = ((CraftPlayer) p);

        GameProfile gp = cp.getProfile();
        gp.getProperties().clear();

        List<Player> players = new ArrayList<>();
        players.addAll(Bukkit.getOnlinePlayers());
        players.remove(p);

        getField(GameProfile.class, "name").set(cp.getProfile(), nickname);

        new Thread(()-> nick.setSkin(p.getUniqueId(), nickname), "Nick-"+p.getUniqueId().toString().replace("-", "")+"-Thread").start();

//        for (Player all : Bukkit.getOnlinePlayers()) {
//            p.hidePlayer(all);
//            all.hidePlayer(p);
//        }

        PacketPlayOutPlayerInfo remove = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, cp.getHandle());
        Reflection.sendAllPacket(remove);

        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(cp.getEntityId());
        Reflection.sendListPacket(players, destroy);

//        PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook packetPlayOutRelEntityMoveLook =
//                new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(p.getEntityId(),
//                        (long) loc.getX(), (long) loc.getY(), (long) loc.getZ(),
//                        (byte) loc.getYaw(), (byte) loc.getPitch(), p.isOnGround());
//        Reflection.sendPlayerPacket(p, packetPlayOutRelEntityMoveLook);

		nick.locs.put(p.getUniqueId(), p.getLocation());
        nick.performDeath(p);

        PacketPlayOutPlayerInfo add = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, cp.getHandle());
        Reflection.sendAllPacket(add);

        PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(cp.getHandle());
        Reflection.sendListPacket(players, spawn);

//        for (org.bukkit.Chunk chunk : loc.getWorld().getLoadedChunks()) {
//            PacketPlayOutMapChunk packetPlayOutMapChunk = new PacketPlayOutMapChunk(((CraftChunk) chunk).getHandle(), 20);
//            Reflection.sendPlayerPacket(p, packetPlayOutMapChunk);
//        }


//        new BukkitRunnable() {
//
//
//            @Override
//            public void run() {
//
//                for (Player all : Bukkit.getOnlinePlayers()) {
//                    p.showPlayer(all);
//                    all.showPlayer(p);
//                }
//
//            }
//        }.runTaskLater(GameChest.getInstance(), 20L);
        System.out.println("[GCG/Nick] Player " + p.getCustomName() + " is now nicked as " + nickname + " = " + p.toString());
    }


    public void unnickPlayer(Player p) throws Exception {
        CraftPlayer cp = (CraftPlayer) p;

        GameProfile gp = cp.getProfile();
        gp.getProperties().clear();

        List<Player> players = new ArrayList<>();
        players.addAll(Bukkit.getOnlinePlayers());
        players.remove(p);

        getField(GameProfile.class, "name").set(cp.getProfile(), p.getCustomName());
        new Thread(()-> nick.resetSkin(p.getUniqueId()), "Unnick-"+p.getUniqueId().toString().replace("-", "")+"-Thread").start();

//        for (Player all : Bukkit.getOnlinePlayers()) {
//            p.hidePlayer(all);
//            all.hidePlayer(p);
//        }

        PacketPlayOutPlayerInfo remove = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, cp.getHandle());
        Reflection.sendAllPacket(remove);

        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(cp.getEntityId());
        Reflection.sendListPacket(players, destroy);


		nick.locs.put(p.getUniqueId(), p.getLocation());
        nick.performDeath(p);

        PacketPlayOutPlayerInfo add = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, cp.getHandle());
        Reflection.sendAllPacket(add);

        PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(cp.getHandle());
        Reflection.sendListPacket(players, spawn);

//        for (org.bukkit.Chunk chunk : loc.getWorld().getLoadedChunks()) {
//            PacketPlayOutMapChunk packetPlayOutMapChunk = new PacketPlayOutMapChunk(((CraftChunk) chunk).getHandle(), 20);
//            Reflection.sendPlayerPacket(p, packetPlayOutMapChunk);
//        }

//        new BukkitRunnable() {
//
//            @Override
//            public void run() {
//                for (Player all : Bukkit.getOnlinePlayers()) {
//                    p.showPlayer(all);
//                    all.showPlayer(p);
//                }
//            }
//        }.runTaskLater(GameChest.getInstance(), 20L);

//		p.teleport(oldLoc);
        System.out.println("[GCG/Nick] Player " + p.getName() + " is now unnicked = " + p.toString());
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
