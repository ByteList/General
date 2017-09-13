package de.gamechest.database.stats.shulkerdefence;

import com.mongodb.BasicDBObject;
import com.mongodb.CursorType;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import de.gamechest.database.DatabaseCollection;
import de.gamechest.database.DatabaseElement;
import de.gamechest.database.DatabaseManager;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.stats.deathrun.DatabaseDeathRunObject;
import org.bson.Document;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by ByteList on 11.04.2017.
 */
public class DatabaseShulkerDefence {

    private final DatabaseManager databaseManager;
    private final DatabaseCollection databaseCollection = DatabaseCollection.SD_STATISTICS;

    public DatabaseShulkerDefence(DatabaseManager databaseManager) {
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

            for (DatabaseShulkerDefenceObject databaseShulkerDefenceObject : DatabaseShulkerDefenceObject.values()) {
                if (databaseShulkerDefenceObject == DatabaseShulkerDefenceObject.UUID)
                    document.put(DatabaseShulkerDefenceObject.UUID.getName(), uuid.toString());
                else
                    document.put(databaseShulkerDefenceObject.getName(), 0);
            }

            databaseManager.getCollection(databaseCollection).insertOne(document);
        });
    }

    public void setDatabaseObject(UUID uuid, DatabaseShulkerDefenceObject databaseShulkerDefenceObject, Object value) {
        this.databaseManager.getAsync().getExecutor().execute(() -> {
            String uid = uuid.toString();
            BasicDBObject doc = new BasicDBObject();
            doc.append("$set", new BasicDBObject().append(databaseShulkerDefenceObject.getName(), value));

            BasicDBObject basicDBObject = new BasicDBObject().append(DatabasePlayerObject.UUID.getName(), uid);
            databaseManager.getCollection(databaseCollection).updateOne(basicDBObject, doc);
        });
    }

    public DatabaseElement getDatabaseElement(UUID uuid, DatabaseShulkerDefenceObject databaseShulkerDefenceObject) {
        String uid = uuid.toString();
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find(Filters.eq(DatabasePlayerObject.UUID.getName(), uid));
        Document document = find.first();

        if (document == null) return null;

        return new DatabaseElement(document.get(databaseShulkerDefenceObject.getName()));
    }

    public ArrayList<UUID> getPlayers() {
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find();
        find.cursorType(CursorType.NonTailable);
        find.projection(Projections.include(DatabaseShulkerDefenceObject.UUID.getName()));
        ArrayList<UUID> list = new ArrayList<>();
        for (Document document : find) {
            list.add(UUID.fromString(document.getString(DatabaseShulkerDefenceObject.UUID.getName())));
        }
        return list;
    }
}
