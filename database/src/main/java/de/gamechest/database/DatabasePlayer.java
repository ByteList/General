package de.gamechest.database;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import de.gamechest.database.rank.Rank;
import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

/**
 * Created by ByteList on 09.04.2017.
 */
public class DatabasePlayer {

    private final DatabaseManager databaseManager;
    private final DatabaseCollection databaseCollection = DatabaseCollection.PLAYERS;

    private final UUID uuid;
    private final FindIterable<Document> find;

    private int exists;

    public DatabasePlayer(DatabaseManager databaseManager, UUID uuid) {
        this.databaseManager = databaseManager;
        this.uuid = uuid;
        this.exists = -1;
        if(uuid != null)
            this.find = databaseManager.getCollection(databaseCollection).find(Filters.eq(DatabasePlayerObject.UUID.getName(), uuid.toString()));
        else
            this.find = null;
    }

    public void setDatabaseObject(DatabasePlayerObject databasePlayerObject, Object value) {
        BasicDBObject doc = new BasicDBObject();
        doc.append("$set", new BasicDBObject().append(databasePlayerObject.getName(), value));

        BasicDBObject basicDBObject = new BasicDBObject().append(DatabasePlayerObject.UUID.getName(), uuid.toString());
        databaseManager.getCollection(databaseCollection).updateOne(basicDBObject, doc);
    }

    public DatabaseElement getDatabaseElement(DatabasePlayerObject databasePlayerObject) {
        if(find == null) return null;
        Document document = find.first();

        if(document == null) return null;

        return new DatabaseElement(document.get(databasePlayerObject.getName()));
    }

    public boolean existsPlayer() {
        if(exists == -1) {
            if(find == null) {
                this.exists = 0;
                return false;
            }
            Document document = find.first();
            if(document != null) {
                this.exists = 1;
                return true;
            }
        } else {
            if(exists == 1)
                return true;
        }
        return false;
    }

    public void createPlayer() {
        if(existsPlayer()) return;



        Calendar now = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        String onlineDate = formatter.format(now.getTime());

        Document lobbyInventory = new Document();
        lobbyInventory.put("NORMAL_KEY", 0);
        lobbyInventory.put("NORMAL_CHEST", 0);

        Document shopItems = new Document();
        shopItems.put(DatabasePlayerObject.ActiveShopItems.HEAD.getName(), 0);
        shopItems.put(DatabasePlayerObject.ActiveShopItems.ARMOR.getName(), 0);
        shopItems.put(DatabasePlayerObject.ActiveShopItems.DUST.getName(), 0);
        shopItems.put(DatabasePlayerObject.ActiveShopItems.GADGET.getName(), 0);

        Document configurations = new Document(); //302.5D,21.0D,2028.5D,2F,65F
        configurations.put(DatabasePlayerObject.Configurations.LOBBY_POS_X.getName(), 302.5);
        configurations.put(DatabasePlayerObject.Configurations.LOBBY_POS_Y.getName(), 21.0);
        configurations.put(DatabasePlayerObject.Configurations.LOBBY_POS_Z.getName(), 2028.5);
        configurations.put(DatabasePlayerObject.Configurations.LOBBY_PITCH.getName(), 2.0);
        configurations.put(DatabasePlayerObject.Configurations.LOBBY_YAW.getName(), 65.0);
        configurations.put(DatabasePlayerObject.Configurations.LOBBY_CHAT.getName(), 0);
        configurations.put(DatabasePlayerObject.Configurations.LOBBY_VISIBILITY.getName(), 0);
        configurations.put(DatabasePlayerObject.Configurations.MSG.getName(), 0);

        Document document = new Document()
            .append(DatabasePlayerObject.UUID.getName(), uuid.toString())
            .append(DatabasePlayerObject.RANK_ID.getName(), Rank.SPIELER.getId())
            .append(DatabasePlayerObject.OPERATOR.getName(), false)
            .append(DatabasePlayerObject.COINS.getName(), 100)
            .append(DatabasePlayerObject.LAST_IP.getName(), null)
            .append(DatabasePlayerObject.LAST_NAME.getName(), null)
            .append(DatabasePlayerObject.BAN_POINTS.getName(), 0)
            .append(DatabasePlayerObject.ONLINE_TIME.getName(), (long) 0)
            .append(DatabasePlayerObject.FIRST_LOGIN.getName(), onlineDate)
            .append(DatabasePlayerObject.LAST_LOGIN.getName(), onlineDate)
            .append(DatabasePlayerObject.TS_UID.getName(), null)
            .append(DatabasePlayerObject.LAST_DAILY_REWARD.getName(), null)
            .append(DatabasePlayerObject.FOUND_SECRETS.getName(), null)
            .append(DatabasePlayerObject.BOUGHT_SHOP_ITEMS.getName(), null)
            .append(DatabasePlayerObject.LOBBY_INVENTORY.getName(), lobbyInventory)
            .append(DatabasePlayerObject.ACTIVE_SHOP_ITEMS.getName(), shopItems)
            .append(DatabasePlayerObject.CONFIGURATIONS.getName(), configurations)
            .append(DatabasePlayerObject.SKIN_TEXTURE.getName(), null);

        databaseManager.getCollection(databaseCollection).insertOne(document);
    }

    public void updatePlayer() {
        Document shopItems = getDatabaseElement(DatabasePlayerObject.ACTIVE_SHOP_ITEMS).getAsDocument();
        if(!shopItems.containsKey(DatabasePlayerObject.ActiveShopItems.GADGET.getName())) {
            shopItems.put(DatabasePlayerObject.ActiveShopItems.GADGET.getName(), 0);
            setDatabaseObject(DatabasePlayerObject.ACTIVE_SHOP_ITEMS, shopItems);
        }

    }
}
