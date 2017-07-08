package de.gamechest.listener;

import de.bytelist.bytecloud.core.ByteCloudCore;
import de.gamechest.BountifulAPI;
import de.gamechest.GameChest;
import de.gamechest.Skin;
import de.gamechest.database.DatabaseManager;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.nick.DatabaseNickObject;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayer;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayerObject;
import de.gamechest.database.uuidbuffer.DatabaseUuidBuffer;
import de.gamechest.nick.Nick;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by ByteList on 09.04.2017.
 *
 * Copyright by ByteList - https://bytelist.de/
 */
public class JoinListener {

    private static GameChest gameChest = GameChest.getInstance();

    public static void callFirstOnJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        if(Bukkit.getServerName().contains("nonBungee")) {
            create(p);
        }

        gameChest.getPacketInjector().addPlayer(p);

        gameChest.getDatabaseManager().getAsync().getOnlinePlayer(p.getUniqueId(), databaseOnlinePlayer -> {
            if(databaseOnlinePlayer.getDatabaseElement(DatabaseOnlinePlayerObject.SERVER_ID).getObject() == null) {
                setServerId(databaseOnlinePlayer);
            } else {
                databaseOnlinePlayer.setDatabaseObject(DatabaseOnlinePlayerObject.PREVIOUS_SERVER_ID, databaseOnlinePlayer.getDatabaseElement(DatabaseOnlinePlayerObject.SERVER_ID).getAsString());
                setServerId(databaseOnlinePlayer);
            }
        });

        BountifulAPI.sendTabTitle(p,
                " §6Game-Chest§f.§6de §8[§b1.9 §f§l- §c1.12§8]  \n"+ //§eSurvival §f& §eSpielmodi
                        "§fAktueller Server: §e"+ (gameChest.isCloudEnabled() ? ByteCloudCore.getInstance().getCloudHandler().getServerId() : Bukkit.getServerName()),

                "§7Willkommen, §c"+p.getName()+"§7!\n"+
                        "  §fInformationen findest du unter §a/help§f!  ");

        Nick nick = gameChest.getNick();

        if(nick.isNicked(p.getUniqueId())) {
            nick.nick(p, nick.getNick(p.getUniqueId()));
        }
    }

    private static void setServerId(DatabaseOnlinePlayer databaseOnlinePlayer) {
        if(gameChest.isCloudEnabled())
            databaseOnlinePlayer.setDatabaseObject(DatabaseOnlinePlayerObject.SERVER_ID, ByteCloudCore.getInstance().getCloudHandler().getServerId());
        else
            databaseOnlinePlayer.setDatabaseObject(DatabaseOnlinePlayerObject.SERVER_ID, Bukkit.getServerName());
    }
    
    private static void create(Player p) {
        DatabaseManager databaseManager = gameChest.getDatabaseManager();

        gameChest.getDatabaseManager().getAsync().getPlayer(p.getUniqueId(), databasePlayer -> {
            databasePlayer.createPlayer();
            databasePlayer.updatePlayer();

            // checking name update
            DatabaseUuidBuffer databaseUuidBuffer = databaseManager.getDatabaseUuidBuffer();
            String lastName = null;
            if(databasePlayer.getDatabaseElement(DatabasePlayerObject.LAST_NAME).getObject() != null)
                lastName = databasePlayer.getDatabaseElement(DatabasePlayerObject.LAST_NAME).getAsString();

            if(lastName == null) {
                databaseUuidBuffer.createPlayer(p.getName(), p.getUniqueId());
            } else if(!lastName.equals(p.getName())) {
                databaseUuidBuffer.removePlayer(lastName);
                databaseUuidBuffer.createPlayer(p.getName(), p.getUniqueId());
            }

            // update database player
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            String onlineDate = formatter.format(Calendar.getInstance().getTime());

            databasePlayer.setDatabaseObject(DatabasePlayerObject.LAST_LOGIN, onlineDate);
            databasePlayer.setDatabaseObject(DatabasePlayerObject.LAST_NAME, p.getName());
            databasePlayer.setDatabaseObject(DatabasePlayerObject.LAST_IP, p.getAddress().getHostString());

            // Skin texture update
            Skin skin = new Skin(p.getUniqueId());
            String value = skin.getSkinValue();
            String signature = skin.getSkinSignature();

            if(value != null && signature != null) {
                Document skinTextures = new Document();
                skinTextures.put(DatabaseNickObject.SkinObject.VALUE.getName(), value);
                skinTextures.put(DatabaseNickObject.SkinObject.SIGNATURE.getName(), signature);
                databasePlayer.setDatabaseObject(DatabasePlayerObject.SKIN_TEXTURE, skinTextures);
            }
        });

        // Online player
        gameChest.getDatabaseManager().getAsync().getOnlinePlayer(p.getUniqueId(), DatabaseOnlinePlayer::createOnlinePlayer);
    }
}
