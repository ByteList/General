package de.gamechest.database.stats.network;

import com.mongodb.BasicDBObject;
import com.mongodb.CursorType;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import de.gamechest.database.DatabaseCollection;
import de.gamechest.database.DatabaseElement;
import de.gamechest.database.DatabaseManager;
import org.bson.Document;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by ByteList on 11.04.2017.
 */
public class DatabaseNetworkStats {

    private final DatabaseManager databaseManager;
    private final DatabaseCollection databaseCollection = DatabaseCollection.NETWORK_STATISTICS;

    public DatabaseNetworkStats(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public boolean existsPlayer(UUID uuid) {
        if(uuid == null) return false;
        String uid = uuid.toString();
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find(Filters.eq(DatabaseNetworkStatsObject.UUID.getName(), uid));
        Document document = find.first();
        return document != null;
    }

    public void createPlayer(UUID uuid) {
        if(existsPlayer(uuid)) return;
        this.databaseManager.getAsync().getExecutor().execute(()-> {
            Document document = new Document();

            for(DatabaseNetworkStatsObject databaseNetworkStatsObject : DatabaseNetworkStatsObject.values()) {
                if(databaseNetworkStatsObject == DatabaseNetworkStatsObject.UUID)
                    document.put(DatabaseNetworkStatsObject.UUID.getName(), uuid.toString());
                else
                    document.put(databaseNetworkStatsObject.getName(), new BasicDBObject());
            }

            databaseManager.getCollection(databaseCollection).insertOne(document);
        });
    }

    public void setDatabaseObject(UUID uuid, DatabaseNetworkStatsObject databaseNetworkStatsObject, BasicDBObject value) {
        this.databaseManager.getAsync().getExecutor().execute(()-> {
            String uid = uuid.toString();
            BasicDBObject doc = new BasicDBObject();
            doc.append("$set", new BasicDBObject().append(databaseNetworkStatsObject.getName(), value));

            BasicDBObject basicDBObject = new BasicDBObject().append(DatabaseNetworkStatsObject.UUID.getName(), uid);
            databaseManager.getCollection(databaseCollection).updateOne(basicDBObject, doc);
        });
    }

    public DatabaseElement getDatabaseElement(UUID uuid, DatabaseNetworkStatsObject databaseNetworkStatsObject) {
        String uid = uuid.toString();
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find(Filters.eq(DatabaseNetworkStatsObject.UUID.getName(), uid));
        find.cursorType(CursorType.NonTailable);
        find.projection(Projections.include(databaseNetworkStatsObject.getName()));
        Document document = find.first();

        if(document == null) return null;

        return new DatabaseElement(document.get(databaseNetworkStatsObject.getName()));
    }

    public ArrayList<UUID> getPlayers() {
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find();
        find.cursorType(CursorType.NonTailable);
        find.projection(Projections.include(DatabaseNetworkStatsObject.UUID.getName()));
        ArrayList<UUID> list = new ArrayList<>();
        for(Document document : find) {
            list.add(UUID.fromString(document.getString(DatabaseNetworkStatsObject.UUID.getName())));
        }
        return list;
    }
}
