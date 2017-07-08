package de.gamechest.database.premiumplayer;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import de.gamechest.database.DatabaseCollection;
import de.gamechest.database.DatabaseElement;
import de.gamechest.database.DatabaseManager;
import de.gamechest.database.DatabasePlayerObject;
import org.bson.Document;

import java.util.UUID;

/**
 * Created by ByteList on 11.04.2017.
 */
public class DatabasePremiumPlayer {

    private final DatabaseManager databaseManager;
    private final DatabaseCollection databaseCollection = DatabaseCollection.PREMIUM_PLAYER;

    public DatabasePremiumPlayer(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public boolean existsPlayer(UUID uuid) {
        String uid = uuid.toString();
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find(Filters.eq(DatabasePlayerObject.UUID.getName(), uid));
        Document document = find.first();
        return document != null;
    }

    public void createPlayer(UUID uuid, long end) {
        if(existsPlayer(uuid)) return;

        Document document = new Document()
                .append(DatabasePremiumPlayerObject.UUID.getName(), uuid.toString())
                .append(DatabasePremiumPlayerObject.ENDING_DATE.getName(), end);


        databaseManager.getCollection(databaseCollection).insertOne(document);
    }

    public void setDatabaseObject(UUID uuid, DatabasePremiumPlayerObject databasePremiumPlayerObject, Object value) {
        String uid = uuid.toString();
        BasicDBObject doc = new BasicDBObject()
                .append("$set", new BasicDBObject().append(databasePremiumPlayerObject.getName(), value));

        BasicDBObject basicDBObject = new BasicDBObject().append(DatabasePlayerObject.UUID.getName(), uid);
        databaseManager.getCollection(databaseCollection).updateOne(basicDBObject, doc);
    }

    public DatabaseElement getDatabaseElement(UUID uuid, DatabasePremiumPlayerObject databasePremiumPlayerObject) {
        String uid = uuid.toString();
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find(Filters.eq(DatabasePlayerObject.UUID.getName(), uid));
        Document document = find.first();

        if(document == null) return null;

        return new DatabaseElement(document.get(databasePremiumPlayerObject.getName()));
    }

    public void removePlayer(UUID uuid) {
        BasicDBObject dbObject = new BasicDBObject()
                .append(DatabasePremiumPlayerObject.UUID.getName(), uuid.toString());
        databaseManager.getCollection(databaseCollection).deleteOne(dbObject);
    }
}
