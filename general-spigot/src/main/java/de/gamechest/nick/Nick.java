package de.gamechest.nick;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mongodb.client.FindIterable;
import de.gamechest.GameChest;
import de.gamechest.database.DatabaseCollection;
import de.gamechest.database.DatabaseManager;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.nick.DatabaseNickObject;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayerObject;
import de.gamechest.nick.event.UserNickEvent;
import de.gamechest.nick.event.UserUnnickEvent;
import net.minecraft.server.v1_9_R2.PacketPlayInClientCommand;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by ByteList on 11.04.2017.
 */
public class Nick {

    private NickPackets packets;
    public List<UUID> list = new ArrayList<>();
    public HashMap<UUID, Location> locs = new HashMap<>();

    private final DatabaseManager databaseManager;

    public final String prefix = "§5Nick §8\u00BB";

    public Nick() {
        packets = new NickPackets(this);

        databaseManager = GameChest.getInstance().getDatabaseManager();
    }

    public void nick(Player p) {
        nick(p, getRandomNickName());
    }

    public void nick(Player p, String nickname) {
        p.setCustomName(p.getName());
        databaseManager.getDatabaseNick().setDatabaseObject(nickname, DatabaseNickObject.USED, p.getName());
        databaseManager.getDatabaseOnlinePlayer(p.getUniqueId()).setDatabaseObject(DatabaseOnlinePlayerObject.NICKNAME, nickname);
        try {
            packets.nickPlayer(p, nickname);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bukkit.getPluginManager().callEvent(new UserNickEvent(p, p.getCustomName(), p.getName()));
    }

    public void unNick(Player p) {
        if(!isNicked(p.getUniqueId()))
            return;
        databaseManager.getDatabaseNick().setDatabaseObject(p.getName(), DatabaseNickObject.USED, false);
        databaseManager.getDatabaseOnlinePlayer(p.getUniqueId()).setDatabaseObject(DatabaseOnlinePlayerObject.NICKNAME, null);
        try {
            packets.unnickPlayer(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
        p.setCustomName(null);
        Bukkit.getPluginManager().callEvent(new UserUnnickEvent(p));
    }

    public void unnickOnDisconnect(Player p) {
        if(isNicked(p.getUniqueId())) {
            String nick = databaseManager.getDatabaseOnlinePlayer(p.getUniqueId()).getDatabaseElement(DatabaseOnlinePlayerObject.NICKNAME).getAsString();
            databaseManager.getDatabaseNick().setDatabaseObject(nick, DatabaseNickObject.USED, false);
        }
    }

    public boolean isNicked(UUID uuid) {
        return databaseManager.getDatabaseOnlinePlayer(uuid).getDatabaseElement(DatabaseOnlinePlayerObject.NICKNAME).getObject() != null;
    }

    public String getNick(UUID uuid) {
        return databaseManager.getDatabaseOnlinePlayer(uuid).getDatabaseElement(DatabaseOnlinePlayerObject.NICKNAME).getAsString();
    }

    public String getPlayernameFromNick(String nick) {
        return databaseManager.getDatabaseNick().getDatabaseElement(DatabaseNickObject.NICK, nick, DatabaseNickObject.USED).getAsString();
    }

    public List<String> getNickedPlayers() {
        FindIterable<Document> find = databaseManager.getCollection(DatabaseCollection.ONLINE_PLAYER).find();
        List<String> list = new ArrayList<>();
        for(Document document : find) {
            UUID uuid = UUID.fromString(document.getString(DatabaseOnlinePlayerObject.UUID.getName()));
            if(isNicked(uuid))
                list.add(document.getString(DatabaseOnlinePlayerObject.NAME.getName()));
        }
        return list;
    }

    public String getRandomNickName() {
        return databaseManager.getDatabaseNick().getRandomNickname();
    }

    public void performDeath(Player p){
//        list.add(p.getUniqueId());
//
//        p.setHealth(0.0D);
//
//        PacketPlayInClientCommand packet = new PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN);
//        ((CraftPlayer) p).getHandle().playerConnection.a(packet);
//        list.remove(p.getUniqueId());
//        locs.remove(p.getUniqueId());
//        Location loc = p.getLocation();
//        System.out.println("Flying:");
//        try {
//            PacketPlayInFlying packetPlayInFlying = new PacketPlayInFlying();
//            Reflection.setValue(packetPlayInFlying, "x", loc.getX());
//            Reflection.setValue(packetPlayInFlying, "y", loc.getY()+0.1);
//            Reflection.setValue(packetPlayInFlying, "z", loc.getZ());
//            Reflection.setValue(packetPlayInFlying, "yaw", loc.getYaw());
//            Reflection.setValue(packetPlayInFlying, "pitch", loc.getPitch());
//            Reflection.sendPlayerPacket(p, packetPlayInFlying);
//        } catch (Exception e) {
//            e.printStackTrace();
////        }
//        Location loc = locs.get(p.getUniqueId());
//        try {
//
////            PacketPlayOutEntity.PacketPlayOutRelEntityMove
////            PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook
////            PacketPlayOutUpdateTime
//
////            PacketPlayOutUnloadChunk packetPlayOutUnloadChunk = new PacketPlayOutUnloadChunk(loc.getChunk().getX(), loc.getChunk().getZ());
////            Reflection.sendPlayerPacket(p, packetPlayOutUnloadChunk);
//
////            PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook packetPlayOutRelEntityMoveLook =
////                    new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(p.getEntityId(),
////                            (long) loc.getX(), (long) loc.getY(), (long) loc.getZ(),
////                            (byte) loc.getYaw(), (byte) loc.getPitch(), p.isOnGround());
//
//
////            Reflection.sendPlayerPacket(p, packetPlayOutRelEntityMoveLook);
//
//            p.setHealth(0.0D);
//            p.spigot().respawn();
//
//            updatePlayer(p);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        this.list.add(p.getUniqueId());

        double oldHealth = p.getHealth();
        double maxHealth = getHealth(p);
        double healthScale = p.getHealthScale();

        p.setHealth(0.0D);

        PacketPlayInClientCommand packet = new PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN);
        ((CraftPlayer)p).getHandle().playerConnection.a(packet);

        p.updateInventory();

        p.getInventory().setHeldItemSlot(p.getInventory().getHeldItemSlot());

        p.resetMaxHealth();
        p.setHealthScale(healthScale);
        p.setMaxHealth(maxHealth);
        p.setHealth(oldHealth);

        p.setItemInHand(p.getItemInHand());
        p.setWalkSpeed(p.getWalkSpeed());

        this.list.remove(p.getUniqueId());
        this.locs.remove(p.getUniqueId());
//        ((CraftPlayer) p).getHandle().playerConnection.teleport();
//        list.remove(p.getUniqueId());
    }

    public void setSkin(UUID uuid, String nick) {
        Player p = Bukkit.getPlayer(uuid);

        GameProfile gp = ((CraftPlayer)p).getProfile();

        String name = "textures";
        Document document = GameChest.getInstance().getDatabaseManager().getDatabaseNick().getDatabaseElement(DatabaseNickObject.NICK, nick, DatabaseNickObject.SKIN_TEXTURE).getAsDocument();
        String value = document.getString(DatabaseNickObject.SkinObject.VALUE.getName());
        String signature = document.getString(DatabaseNickObject.SkinObject.SIGNATURE.getName());

        gp.getProperties().put(name, new Property(name, value, signature));
    }

    public void resetSkin(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);

        GameProfile gp = ((CraftPlayer)p).getProfile();

        String name = "textures";
        Document document = GameChest.getInstance().getDatabaseManager().getDatabasePlayer(uuid).getDatabaseElement(DatabasePlayerObject.SKIN_TEXTURE).getAsDocument();
        String value = document.getString(DatabaseNickObject.SkinObject.VALUE.getName());
        String signature = document.getString(DatabaseNickObject.SkinObject.SIGNATURE.getName());

        gp.getProperties().put(name, new Property(name, value, signature));
    }
//    public void updatePlayer(Player player) throws Exception {
//        CraftPlayer cp = (CraftPlayer) player;
//
//
//
//        Location location = locs.get(player.getUniqueId());
//
////        PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(cp.getEntityId(),
////                EnumDifficulty.getById(player.getWorld().getDifficulty().getValue()),
////                ((CraftWorld) player.getWorld()).getHandle().L(),
////                WorldSettings.EnumGamemode.getById(player.getGameMode().getValue()));
//
////        PacketPlayInClientCommand respawn = new PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN);
//
////
////        PacketPlayOutPosition teleport = new PacketPlayOutPosition(location.getX(), location.getY()+0.1,
////                location.getZ(), location.getYaw(), location.getPitch(), Collections.EMPTY_SET, -1337);
////
////        Reflection.sendPlayerPacket(player, respawn);
////        Reflection.sendPlayerPacket(player, teleport);
//
//
//
//        player.teleport(location);
//
//        locs.remove(player.getUniqueId());
//
//        //https://github.com/games647/ChangeSkin/blob/master/bukkit/src/main/java/com/github/games647/changeskin/bukkit/tasks/SkinUpdater.java
//    }

    private double getHealth(Player player) {
        double health = player.getMaxHealth();
        for(PotionEffect potionEffect : player.getActivePotionEffects()){
            //Had to do this because doing if(potionEffect.getType() == PotionEffectType.HEALTH_BOOST)
            //It wouldn't recognize it as the same.
            if(potionEffect.getType().getName().equalsIgnoreCase(PotionEffectType.HEALTH_BOOST.getName())){
                health -= ((potionEffect.getAmplifier() + 1) * 4);
            }
        }

        return health;
    }
}
