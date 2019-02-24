package de.gamechest.listener;

import de.bytelist.bytecloud.core.ByteCloudCore;
import de.bytelist.bytecloud.database.DatabaseServerObject;
import de.gamechest.BountifulAPI;
import de.gamechest.GameChest;
import de.gamechest.Skin;
import de.gamechest.common.spigot.SpigotChestNick;
import de.gamechest.database.DatabaseManager;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.nick.DatabaseNickObject;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayer;
import de.gamechest.database.terms.DatabaseTermsObject;
import de.gamechest.database.uuidbuffer.DatabaseUuidBuffer;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by ByteList on 09.04.2017.
 *
 * Copyright by ByteList - https://bytelist.de/
 */
public class JoinListener implements Listener {

    private final GameChest gameChest = GameChest.getInstance();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        if(Bukkit.getServerName().contains("nonBungee")) {
            create(player);
        }

        Bukkit.getScheduler().runTaskAsynchronously(gameChest, ()-> gameChest.getPacketInjector().addPlayer(player));

        Bukkit.getScheduler().runTaskAsynchronously(gameChest, ()-> {
            String serverId = Bukkit.getServerName();

            if(gameChest.isCloudEnabled()) {
                String group = ByteCloudCore.getInstance().getCloudHandler().getDatabaseServerValue(
                        ByteCloudCore.getInstance().getCloudHandler().getServerId(), DatabaseServerObject.GROUP).getAsString();
                if(!group.equals("PERMANENT")) {
                    serverId = group +"-" + ByteCloudCore.getInstance().getCloudHandler().getServerId().split("-")[1];
                } else {
                    serverId = ByteCloudCore.getInstance().getCloudHandler().getServerId();
                }
            }

            BountifulAPI.sendTabTitle(player,
                    " §6Game-ChestPrefix§f.§6de §8[§b1.9 §f§l- §c1.12§8]  \n"+
                            "§fAktueller Server: §e"+ serverId,
                    "§7Willkommen, §c"+player.getName()+"§7!\n"+
                            "  §fInformationen findest du unter §a/help§f!  ");
            BountifulAPI.sendTitle(e.getPlayer(), 1, 2, 1, "§r", "§r");
        });

        if(gameChest.getDatabaseManager().getDatabaseTerms().existsPlayer(player.getUniqueId()) &&
                gameChest.getDatabaseManager().getDatabaseTerms().getDatabaseElement(player.getUniqueId(), DatabaseTermsObject.STATE).getAsInt() == 0) {
            return;
        }
        SpigotChestNick nick = gameChest.getNick();

        if(nick.isNicked(player.getUniqueId())) {
            nick.nickOnConnect(player, nick.getNick(player.getUniqueId()));
        }
    }


    @Deprecated
    public static void callFirstOnJoin(PlayerJoinEvent e) {}
    
    private void create(Player p) {
        DatabaseManager databaseManager = gameChest.getDatabaseManager();

        if(!databaseManager.getDatabaseTerms().existsPlayer(p.getUniqueId())) {
            databaseManager.getDatabaseTerms().createPlayer(p.getUniqueId());
            return;
        }

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
