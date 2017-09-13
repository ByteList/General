package de.gamechest.database.stats.deathrun;

import com.mongodb.BasicDBObject;
import com.mongodb.CursorType;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import de.gamechest.database.DatabaseCollection;
import de.gamechest.database.DatabaseElement;
import de.gamechest.database.DatabaseManager;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.stats.clickattack.DatabaseClickAttackObject;
import org.bson.Document;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by ByteList on 11.04.2017.
 */
public class DatabaseDeathRun {

    private final DatabaseManager databaseManager;
    private final DatabaseCollection databaseCollection = DatabaseCollection.DR_STATISTICS;

    public DatabaseDeathRun(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public boolean existsPlayer(UUID uuid) {
        String uid = uuid.toString();
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find(Filters.eq(DatabasePlayerObject.UUID.getName(), uid));
        Document document = find.first();
        return document != null;
    }

    public void createPlayer(UUID uuid) {
        if (existsPlayer(uuid)) return;
        this.databaseManager.getAsync().getExecutor().execute(() -> {

            Document document = new Document();

            for (DatabaseDeathRunObject databaseDeathRunObject : DatabaseDeathRunObject.values()) {
                if (databaseDeathRunObject == DatabaseDeathRunObject.UUID)
                    document.put(DatabaseDeathRunObject.UUID.getName(), uuid.toString());
                else
                    document.put(databaseDeathRunObject.getName(), 0);
            }

            databaseManager.getCollection(databaseCollection).insertOne(document);
        });
    }

    public void setDatabaseObject(UUID uuid, DatabaseDeathRunObject databaseDeathRunObject, Object value) {
        this.databaseManager.getAsync().getExecutor().execute(() -> {
            String uid = uuid.toString();
            BasicDBObject doc = new BasicDBObject();
            doc.append("$set", new BasicDBObject().append(databaseDeathRunObject.getName(), value));

            BasicDBObject basicDBObject = new BasicDBObject().append(DatabasePlayerObject.UUID.getName(), uid);
            databaseManager.getCollection(databaseCollection).updateOne(basicDBObject, doc);
        });
    }

    public DatabaseElement getDatabaseElement(UUID uuid, DatabaseDeathRunObject databaseDeathRunObject) {
        String uid = uuid.toString();
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find(Filters.eq(DatabasePlayerObject.UUID.getName(), uid));
        Document document = find.first();

        if (document == null) return null;

        return new DatabaseElement(document.get(databaseDeathRunObject.getName()));
    }

    public ArrayList<UUID> getPlayers() {
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find();
        find.cursorType(CursorType.NonTailable);
        find.projection(Projections.include(DatabaseDeathRunObject.UUID.getName()));
        ArrayList<UUID> list = new ArrayList<>();
        for (Document document : find) {
            list.add(UUID.fromString(document.getString(DatabaseDeathRunObject.UUID.getName())));
        }
        return list;
    }
}
