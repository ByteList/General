package de.gamechest.nick;

import com.mongodb.client.FindIterable;
import de.gamechest.GameChest;
import de.gamechest.database.DatabaseCollection;
import de.gamechest.database.nick.DatabaseNickObject;
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
            String nick = gameChest.getDatabaseManager().getDatabaseOnlinePlayer(p.getUniqueId()).getDatabaseElement(DatabaseOnlinePlayerObject.NICKNAME).getAsString();
            gameChest.getDatabaseManager().getDatabaseNick().setDatabaseObject(nick, DatabaseNickObject.USED, false);
        }
    }

    public boolean isNicked(UUID uuid) {
        return gameChest.getDatabaseManager().getDatabaseOnlinePlayer(uuid).getDatabaseElement(DatabaseOnlinePlayerObject.NICKNAME).getObject() != null;
    }

    public String getNick(UUID uuid) {
        return gameChest.getDatabaseManager().getDatabaseOnlinePlayer(uuid).getDatabaseElement(DatabaseOnlinePlayerObject.NICKNAME).getAsString();
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
