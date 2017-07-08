package de.gamechest.nick;

import com.mongodb.client.FindIterable;
import de.gamechest.GameChest;
import de.gamechest.database.DatabaseCollection;
import de.gamechest.database.DatabasePlayer;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.nick.DatabaseNickObject;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayer;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayerObject;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by ByteList on 11.04.2017.
 */
public class Nick {


    private final GameChest gameChest;

    public final String prefix = "§5Nick §8\u00BB";

    public Nick() {
        gameChest = GameChest.getInstance();
    }


    public void unnickOnDisconnect(ProxiedPlayer p) {
        if(isNicked(p.getUniqueId())) {
            gameChest.getDatabaseManager().getAsync().getOnlinePlayer(p.getUniqueId(), dbOPLayer->
                gameChest.getDatabaseManager().getDatabaseNick().setDatabaseObject(
                        dbOPLayer.getDatabaseElement(DatabaseOnlinePlayerObject.NICKNAME).getAsString(), DatabaseNickObject.USED, false));
        }
    }

    public boolean isNicked(UUID uuid) {
        return new DatabaseOnlinePlayer(gameChest.getDatabaseManager(), uuid.toString(),
            new DatabasePlayer(gameChest.getDatabaseManager(), uuid).getDatabaseElement(DatabasePlayerObject.LAST_NAME).getAsString())
            .getDatabaseElement(DatabaseOnlinePlayerObject.NICKNAME).getObject() != null;
    }

    public List<String> getNickedNames() {
        FindIterable<Document> find = gameChest.getDatabaseManager().getCollection(DatabaseCollection.ONLINE_PLAYER).find();
        List<String> list = new ArrayList<>();
        for(Document document : find) {
            UUID uuid = UUID.fromString(document.getString(DatabaseOnlinePlayerObject.UUID.getName()));
            if(isNicked(uuid))
                list.add(document.getString(DatabaseOnlinePlayerObject.NICKNAME.getName()));
        }
        return list;
    }

    public boolean isNameANick(String name) {
        return getNickedNames().contains(name);
    }

    public String getNick(UUID uuid) {
        return new DatabaseOnlinePlayer(gameChest.getDatabaseManager(), uuid.toString(),
                new DatabasePlayer(gameChest.getDatabaseManager(), uuid).getDatabaseElement(DatabasePlayerObject.LAST_NAME).getAsString())
                .getDatabaseElement(DatabaseOnlinePlayerObject.NICKNAME).getAsString();
    }

    public String getPlayernameFromNick(String nick) {
        return gameChest.getDatabaseManager().getDatabaseNick().getDatabaseElement(DatabaseNickObject.NICK, nick, DatabaseNickObject.USED).getAsString();
    }

    public List<String> getNickedPlayers() {
        FindIterable<Document> find = gameChest.getDatabaseManager().getCollection(DatabaseCollection.ONLINE_PLAYER).find();
        List<String> list = new ArrayList<>();
        for(Document document : find) {
            UUID uuid = UUID.fromString(document.getString(DatabaseOnlinePlayerObject.UUID.getName()));
            if(isNicked(uuid))
                list.add(document.getString(DatabaseOnlinePlayerObject.NAME.getName()));
        }
        return list;
    }
}
