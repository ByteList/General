package de.gamechest.nick;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mongodb.client.FindIterable;
import de.gamechest.GameChest;
import de.gamechest.database.DatabaseCollection;
import de.gamechest.database.DatabaseManager;
import de.gamechest.database.DatabasePlayer;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.nick.DatabaseNickObject;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayer;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayerObject;
import de.gamechest.nick.event.UserNickEvent;
import de.gamechest.nick.event.UserUnnickEvent;
import net.minecraft.server.v1_9_R2.EntityPlayer;
import net.minecraft.server.v1_9_R2.PacketPlayOutRespawn;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by ByteList on 11.04.2017.
 */
public class Nick {

    private NickPackets packets;

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
        databaseManager.getAsync().getOnlinePlayer(p.getUniqueId(), databaseOnlinePlayer-> databaseOnlinePlayer.setDatabaseObject(DatabaseOnlinePlayerObject.NICKNAME, nickname), DatabaseOnlinePlayerObject.NICKNAME);
        try {
            packets.nickPlayer(p, nickname);
        } catch (Exception e) {
            e.printStackTrace();
        }
        p.sendMessage(GameChest.getInstance().getNick().prefix + "§bDein Nickname ist nun: §9"+nickname);
        Bukkit.getPluginManager().callEvent(new UserNickEvent(p, p.getCustomName(), p.getName()));
    }

    public void unNick(Player p) {
        if(!isNicked(p.getUniqueId()))
            return;
        databaseManager.getDatabaseNick().setDatabaseObject(p.getName(), DatabaseNickObject.USED, false);
        databaseManager.getAsync().getOnlinePlayer(p.getUniqueId(), databaseOnlinePlayer-> databaseOnlinePlayer.setDatabaseObject(DatabaseOnlinePlayerObject.NICKNAME, null), DatabaseOnlinePlayerObject.NICKNAME);
        try {
            packets.unnickPlayer(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
        p.setCustomName(null);
        p.sendMessage(GameChest.getInstance().getNick().prefix + "§bDein Nickname wurde zurückgesetzt.");
        Bukkit.getPluginManager().callEvent(new UserUnnickEvent(p));
    }

    public void unnickOnDisconnect(Player p) {
        if(isNicked(p.getUniqueId())) {
            databaseManager.getAsync().getOnlinePlayer(p.getUniqueId(), dbOPlayer-> {
                String nick = dbOPlayer.getDatabaseElement(DatabaseOnlinePlayerObject.NICKNAME).getAsString();
                databaseManager.getDatabaseNick().setDatabaseObject(nick, DatabaseNickObject.USED, false);
            }, DatabaseOnlinePlayerObject.NICKNAME);
        }
    }

    public boolean isNicked(UUID uuid) {
        try {
            return new DatabaseOnlinePlayer(databaseManager, uuid.toString(),
                    new DatabasePlayer(databaseManager, uuid).getDatabaseElement(DatabasePlayerObject.LAST_NAME).getAsString())
                    .getDatabaseElement(DatabaseOnlinePlayerObject.NICKNAME).getObject() != null;
        } catch (Exception ex) {
            return false;
        }
    }

    public String getNick(UUID uuid) {
        return new DatabaseOnlinePlayer(databaseManager, uuid.toString(),
                new DatabasePlayer(databaseManager, uuid).getDatabaseElement(DatabasePlayerObject.LAST_NAME).getAsString())
                .getDatabaseElement(DatabaseOnlinePlayerObject.NICKNAME).getAsString();
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

    void performDeath(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();

        final boolean flying = player.isFlying();
        final double oldHealth = player.getHealth();
        final double maxHealth = getHealth(player);
        final double healthScale = player.getHealthScale();
        final int foodLevel = player.getFoodLevel();
        final int level = player.getLevel();
        final float xp = player.getExp();
        final ItemStack[] armorContents = player.getInventory().getArmorContents();

        player.getInventory().setArmorContents(null);

        PacketPlayOutRespawn packet = new PacketPlayOutRespawn(player.getWorld().getEnvironment().getId(),
                entityPlayer.getWorld().getDifficulty(), entityPlayer.getWorld().getWorldData().getType(), entityPlayer.playerInteractManager.getGameMode());
        entityPlayer.playerConnection.sendPacket(packet);

        entityPlayer.playerConnection.teleport(new Location(player.getWorld(), entityPlayer.locX, entityPlayer.locY+0.1, entityPlayer.locZ, entityPlayer.yaw, entityPlayer.pitch));
        Chunk chunk = player.getWorld().getChunkAt(player.getLocation());
        for (int x = -10; x < 10; x++) {
            for (int z = -10; z < 10; z++) {
//                player.getWorld().refreshChunk(chunk.getX() + x, chunk.getZ() + z);
                player.getWorld().unloadChunk(chunk.getX() + x, chunk.getZ() + z);
                player.getWorld().loadChunk(chunk.getX() + x, chunk.getZ() + z);
            }
        }
        player.getInventory().setArmorContents(armorContents);
        player.updateInventory();

        player.getInventory().setHeldItemSlot(player.getInventory().getHeldItemSlot());

        player.setFlying(flying);
        player.resetMaxHealth();
        player.setHealthScale(healthScale);
        player.setMaxHealth(maxHealth);
        player.setHealth(oldHealth);
        player.setFoodLevel(foodLevel);
        player.setLevel(level);
        player.setExp(xp);
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
        databaseManager.getAsync().getPlayer(uuid, dbPlayer-> {
            Document document = dbPlayer.getDatabaseElement(DatabasePlayerObject.SKIN_TEXTURE).getAsDocument();
            String value = document.getString(DatabaseNickObject.SkinObject.VALUE.getName());
            String signature = document.getString(DatabaseNickObject.SkinObject.SIGNATURE.getName());

            gp.getProperties().put(name, new Property(name, value, signature));
        }, DatabasePlayerObject.SKIN_TEXTURE);
    }

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
