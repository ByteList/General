package de.gamechest.listener;

import de.gamechest.ConnectManager;
import de.gamechest.GameChest;
import de.gamechest.Skin;
import de.gamechest.database.DatabaseManager;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.ban.DatabaseBanObject;
import de.gamechest.database.nick.DatabaseNickObject;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayer;
import de.gamechest.database.rank.Rank;
import de.gamechest.database.uuidbuffer.DatabaseUuidBuffer;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by ByteList on 10.04.2017.
 */
public class LoginListener implements Listener {
    
    private GameChest gameChest = GameChest.getInstance();
    private DatabaseManager databaseManager = gameChest.getDatabaseManager();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(LoginEvent e) {
        PendingConnection pc = e.getConnection();
        ConnectManager.ConnectState currentConnectState = gameChest.getConnectManager().getConnectState();

        switch (currentConnectState) {

            case OPEN:
                break;
            case WHITELIST:
                if(!gameChest.getConnectManager().getWhiteList().contains(pc.getUniqueId())) {
                    e.setCancelled(true);
                    e.setCancelReason("§cWir befinden uns momentan im §6geschlossenen Modus§c!");
                    return;
                }
                break;
            case MAINTENANCE:
                if (!gameChest.hasRank(pc.getUniqueId(), Rank.BUILDER)) {
                    e.setCancelled(true);
                    e.setCancelReason("§cWir befinden uns momentan im §aWartungsmodus§c!");
                    return;
                }
                break;
            case DEVELOPMENT:
                if (!gameChest.hasRank(pc.getUniqueId(), Rank.DEVELOPER)) {
                    e.setCancelled(true);
                    e.setCancelReason("§cWir befinden uns momentan im §4Development-Modus§c!");
                    return;
                }
                break;
        }

        if(gameChest.getProxy().getPlayers().size() >= gameChest.getConnectManager().getPlayerLimit()) {
            if (!gameChest.hasRank(pc.getUniqueId(), Rank.PREMIUM)) {
                e.setCancelled(true);
                e.setCancelReason("§cWir haben unser Spielerlimit erreicht!\n\n" +
                        "Um trotzdem joinen zu können, musst du einen Premium-Rang besitzen.");
                return;
            }
        }

        if(databaseManager.getDatabaseBan().isBanned(pc.getUniqueId())) {
            if(isBanned(pc.getUniqueId())) {
                e.setCancelled(true);
                e.setCancelReason(gameChest.getBanMessage(pc.getUniqueId()));
                return;
            }
        }

        // database player
        databaseManager.getAsync().getPlayer(pc.getUniqueId(), dbPlayer -> {
            dbPlayer.createPlayer();
            dbPlayer.updatePlayer();

            DatabaseUuidBuffer databaseUuidBuffer = databaseManager.getDatabaseUuidBuffer();
            String lastName = null;
            if(dbPlayer.getDatabaseElement(DatabasePlayerObject.LAST_NAME).getObject() != null)
                lastName = dbPlayer.getDatabaseElement(DatabasePlayerObject.LAST_NAME).getAsString();

            if(lastName == null) {
                databaseUuidBuffer.createPlayer(pc.getName(), pc.getUniqueId());
            } else if(!lastName.equals(pc.getName())) {
                databaseUuidBuffer.removePlayer(lastName);
                databaseUuidBuffer.createPlayer(pc.getName(), pc.getUniqueId());
            }

            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            String onlineDate = formatter.format(Calendar.getInstance().getTime());

            dbPlayer.setDatabaseObject(DatabasePlayerObject.LAST_LOGIN, onlineDate);
            dbPlayer.setDatabaseObject(DatabasePlayerObject.LAST_NAME, pc.getName());
            dbPlayer.setDatabaseObject(DatabasePlayerObject.LAST_IP, pc.getAddress().getHostString());

            Skin skin = new Skin(pc.getUniqueId());
            String value = skin.getSkinValue();
            String signature = skin.getSkinSignature();

            if(value != null && signature != null) {
                Document skinTextures = new Document();
                skinTextures.put(DatabaseNickObject.SkinObject.VALUE.getName(), value);
                skinTextures.put(DatabaseNickObject.SkinObject.SIGNATURE.getName(), signature);
                dbPlayer.setDatabaseObject(DatabasePlayerObject.SKIN_TEXTURE, skinTextures);
            }
        });

        // online database player
        databaseManager.getAsync().getOnlinePlayer(pc.getUniqueId(), DatabaseOnlinePlayer::createOnlinePlayer);
    }

    private boolean isBanned(UUID uuid) {
        Date d = new Date();
        Date cl = new Date();

        String end = databaseManager.getDatabaseBan().getDatabaseElement(uuid, DatabaseBanObject.END_DATE).getAsString();
        try {
            if (Long.valueOf(end) == -1L) return true;
        } catch (NumberFormatException localNumberFormatException) {
            end = end.replace(".", ":");

            String[] endArray = end.split(" ");

            String[] endDatum = endArray[0].split(":");
            String[] endzeit = endArray[1].split(":");

            cl.setDate(Integer.valueOf(endDatum[0]));
            cl.setMonth(Integer.valueOf(endDatum[1]) - 1);
            cl.setYear(Integer.valueOf(endDatum[2]) - 1900);

            cl.setHours(Integer.valueOf(endzeit[0]));
            cl.setMinutes(Integer.valueOf(endzeit[1]));
        }
        return d.before(cl);
    }
}
