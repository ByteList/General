package de.gamechest.database.stats.jumpduell;

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
public class DatabaseJumpDuell {

    private final DatabaseManager databaseManager;
    private final DatabaseCollection databaseCollection = DatabaseCollection.JD_STATISTICS;

    public DatabaseJumpDuell(DatabaseManager databaseManager) {
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

            for (DatabaseJumpDuellObject databaseJumpDuellObject : DatabaseJumpDuellObject.values()) {
                if (databaseJumpDuellObject == DatabaseJumpDuellObject.UUID)
                    document.put(DatabaseJumpDuellObject.UUID.getName(), uuid.toString());
                else
                    document.put(databaseJumpDuellObject.getName(), 0);
            }

            databaseManager.getCollection(databaseCollection).insertOne(document);
        });
    }

    public void setDatabaseObject(UUID uuid, DatabaseJumpDuellObject databaseJumpDuellObject, Object value) {
        this.databaseManager.getAsync().getExecutor().execute(() -> {
            String uid = uuid.toString();
            BasicDBObject doc = new BasicDBObject();
            doc.append("$set", new BasicDBObject().append(databaseJumpDuellObject.getName(), value));

            BasicDBObject basicDBObject = new BasicDBObject().append(DatabasePlayerObject.UUID.getName(), uid);
            databaseManager.getCollection(databaseCollection).updateOne(basicDBObject, doc);
        });
    }

    public DatabaseElement getDatabaseElement(UUID uuid, DatabaseJumpDuellObject databaseJumpDuellObject) {
        String uid = uuid.toString();
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find(Filters.eq(DatabasePlayerObject.UUID.getName(), uid));
        Document document = find.first();

        if (document == null) return null;

        return new DatabaseElement(document.get(databaseJumpDuellObject.getName()));
    }

    public ArrayList<UUID> getPlayers() {
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find();
        find.cursorType(CursorType.NonTailable);
        find.projection(Projections.include(DatabaseJumpDuellObject.UUID.getName()));
        ArrayList<UUID> list = new ArrayList<>();
        for (Document document : find) {
            list.add(UUID.fromString(document.getString(DatabaseJumpDuellObject.UUID.getName())));
        }
        return list;
    }
}
