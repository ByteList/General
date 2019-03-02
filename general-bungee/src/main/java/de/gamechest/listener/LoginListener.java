package de.gamechest.listener;

import com.mongodb.BasicDBObject;
import de.gamechest.ConnectManager;
import de.gamechest.GameChest;
import de.gamechest.Skin;
import de.gamechest.common.AsyncTasks;
import de.gamechest.database.DatabaseManager;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.ban.DatabaseBanObject;
import de.gamechest.database.nick.DatabaseNickObject;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayer;
import de.gamechest.common.Rank;
import de.gamechest.database.stats.network.DatabaseNetworkStatsObject;
import de.gamechest.database.uuidbuffer.DatabaseUuidBuffer;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import org.bson.BsonInt64;
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
        PendingConnection connection = e.getConnection();
        ConnectManager.ConnectState currentConnectState = gameChest.getConnectManager().getConnectState();

        switch (currentConnectState) {

            case OPEN:
                String eventServer = gameChest.getConnectManager().getEventServer();
                if(!eventServer.equals("---")) {
                    if (!gameChest.getProxy().getServers().containsKey(eventServer) && !gameChest.hasRank(connection.getUniqueId(), Rank.BUILDER)) {
                        e.setCancelled(true);
                        e.setCancelReason("§cDer Event-Server konnte nicht erreicht werden!");
                        return;
                    }
                }
                break;
            case WHITELIST:
                if(!gameChest.getConnectManager().getWhiteList().contains(connection.getUniqueId())) {
                    e.setCancelled(true);
                    e.setCancelReason("§cWir befinden uns momentan im §6geschlossenen Modus§c!");
                    return;
                }
                break;
            case MAINTENANCE:
                if (!gameChest.hasRank(connection.getUniqueId(), Rank.BUILDER)) {
                    e.setCancelled(true);
                    e.setCancelReason("§cWir befinden uns momentan im §aWartungsmodus§c!");
                    return;
                }
                break;
            case DEVELOPMENT:
                if (!gameChest.hasRank(connection.getUniqueId(), Rank.DEVELOPER)) {
                    e.setCancelled(true);
                    e.setCancelReason("§cWir befinden uns momentan im §4Development-Modus§c!");
                    return;
                }
                break;
        }

        if(gameChest.getProxy().getPlayers().size() >= gameChest.getConnectManager().getPlayerLimit()) {
            if (!gameChest.hasRank(connection.getUniqueId(), Rank.PREMIUM)) {
                e.setCancelled(true);
                e.setCancelReason("§cWir haben unser Spielerlimit erreicht!\n\n" +
                        "Um trotzdem joinen zu können, musst du einen Premium-Rang besitzen.");
                return;
            }
        }

        if(gameChest.getProxy().getPlayer(connection.getUniqueId()) != null) {
            e.setCancelled(true);
            e.setCancelReason("§cDer Account ist schon auf dem Netzwerk online!");
            return;
        }

        if(databaseManager.getDatabaseBan().isBanned(connection.getUniqueId())) {
            if(isBanned(connection.getUniqueId())) {
                e.setCancelled(true);
                e.setCancelReason(gameChest.getBanMessage(connection.getUniqueId()));
                return;
            }
        }

        if(!databaseManager.getDatabaseTerms().existsPlayer(connection.getUniqueId())) {
            databaseManager.getDatabaseTerms().createPlayer(connection.getUniqueId());
            gameChest.getPreLogin().add(connection.getUniqueId());
            return;
        }

        // database player
        databaseManager.getAsync().getPlayer(connection.getUniqueId(), dbPlayer -> {
            dbPlayer.createPlayer();
            dbPlayer.updatePlayer();

            DatabaseUuidBuffer databaseUuidBuffer = databaseManager.getDatabaseUuidBuffer();
            String lastName = null;
            if(dbPlayer.getDatabaseElement(DatabasePlayerObject.LAST_NAME).getObject() != null)
                lastName = dbPlayer.getDatabaseElement(DatabasePlayerObject.LAST_NAME).getAsString();

            if(lastName == null) {
                databaseUuidBuffer.createPlayer(connection.getName(), connection.getUniqueId());
            } else if(!lastName.equals(connection.getName())) {
                databaseUuidBuffer.removePlayer(lastName);
                databaseUuidBuffer.createPlayer(connection.getName(), connection.getUniqueId());
            }

            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            String onlineDate = formatter.format(Calendar.getInstance().getTime());

            dbPlayer.setDatabaseObject(DatabasePlayerObject.LAST_LOGIN, onlineDate);
            dbPlayer.setDatabaseObject(DatabasePlayerObject.LAST_NAME, connection.getName());
            dbPlayer.setDatabaseObject(DatabasePlayerObject.LAST_IP, connection.getAddress().getHostString());

            Skin skin = new Skin(connection.getUniqueId());
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
        databaseManager.getAsync().getOnlinePlayer(connection.getUniqueId(), connection.getName(), DatabaseOnlinePlayer::createOnlinePlayer);


        final String finalStatistic = "net.connection";
        AsyncTasks.getInstance().runTaskAsync(()-> {
            databaseManager.getDatabaseNetworkStats().createPlayer(connection.getUniqueId());

            BasicDBObject document = gameChest.getDatabaseManager().getDatabaseNetworkStats().
                    getDatabaseElement(connection.getUniqueId(), DatabaseNetworkStatsObject.NETWORK).getAsBasicDBObject();

            int value = 0;
            if(document.containsKey(finalStatistic)) {
                value = document.getInt(finalStatistic);
            }

            document.append(finalStatistic, new BsonInt64(value+1));

            gameChest.getDatabaseManager().getDatabaseNetworkStats().setDatabaseObject(connection.getUniqueId(), DatabaseNetworkStatsObject.NETWORK, document);
        });
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
