package de.gamechest.database.onlineplayer;

import com.mongodb.BasicDBObject;
import com.mongodb.CursorType;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import de.gamechest.database.DatabaseCollection;
import de.gamechest.database.DatabaseElement;
import de.gamechest.database.DatabaseManager;
import org.bson.Document;

import java.util.UUID;

/**
 * Created by ByteList on 11.04.2017.
 *
 * Copyright by ByteList - https://bytelist.de/
 */
public class DatabaseOnlinePlayer {

    private final DatabaseManager databaseManager;
    private final DatabaseCollection databaseCollection = DatabaseCollection.ONLINE_PLAYER;

    private final String uuid;
    private final String name;
    private final FindIterable<Document> find;

    public DatabaseOnlinePlayer(DatabaseManager databaseManager, String uuid, String name) {
        this.databaseManager = databaseManager;
        this.uuid = uuid;
        this.name = name;
        this.find = databaseManager.getCollection(databaseCollection).find(Filters.eq(DatabaseOnlinePlayerObject.UUID.getName(), uuid));
    }

    public DatabaseOnlinePlayer(DatabaseManager databaseManager, String uuid, String name, DatabaseOnlinePlayerObject... accesses) {
        this.databaseManager = databaseManager;
        this.uuid = uuid;
        this.name = name;
        this.find = databaseManager.getCollection(databaseCollection).find(Filters.eq(DatabaseOnlinePlayerObject.UUID.getName(), uuid));
        this.find.cursorType(CursorType.NonTailable);
        this.find.projection(Projections.include(DatabaseOnlinePlayerObject.toStringList(accesses)));
    }

    public DatabaseOnlinePlayer(DatabaseManager databaseManager, UUID uuid) {
        this.databaseManager = databaseManager;
        this.uuid = uuid.toString();
        this.name = null;
        this.find = databaseManager.getCollection(databaseCollection).find(Filters.eq(DatabaseOnlinePlayerObject.UUID.getName(), this.uuid));
    }

    public DatabaseOnlinePlayer(DatabaseManager databaseManager, UUID uuid, DatabaseOnlinePlayerObject... accesses) {
        this.databaseManager = databaseManager;
        this.uuid = uuid.toString();
        this.name = null;
        this.find = databaseManager.getCollection(databaseCollection).find(Filters.eq(DatabaseOnlinePlayerObject.UUID.getName(), this.uuid));
        this.find.cursorType(CursorType.NonTailable);
        this.find.projection(Projections.include(DatabaseOnlinePlayerObject.toStringList(accesses)));
    }

    public void setDatabaseObject(DatabaseOnlinePlayerObject databaseOnlinePlayerObject, Object value) {
        BasicDBObject doc = new BasicDBObject()
            .append("$set", new BasicDBObject().append(databaseOnlinePlayerObject.getName(), value));

        BasicDBObject basicDBObject = new BasicDBObject().append(DatabaseOnlinePlayerObject.UUID.getName(), uuid);
        databaseManager.getCollection(databaseCollection).updateOne(basicDBObject, doc);
    }

    public DatabaseElement getDatabaseElement(DatabaseOnlinePlayerObject databaseOnlinePlayerObject) {
        Document document = find.first();

        if(document == null) return null;

        return new DatabaseElement(document.get(databaseOnlinePlayerObject.getName()));
    }

    public boolean isOnline() {
        Document document = find.first();
        return document != null;
    }

    public void createOnlinePlayer() {
        if(isOnline()) return;

        Document document = new Document()
            .append(DatabaseOnlinePlayerObject.UUID.getName(), uuid)
            .append(DatabaseOnlinePlayerObject.NAME.getName(), name)
            .append(DatabaseOnlinePlayerObject.SERVER_ID.getName(), null)
            .append(DatabaseOnlinePlayerObject.PREVIOUS_SERVER_ID.getName(), null)
            .append(DatabaseOnlinePlayerObject.NICKNAME.getName(), null)
            .append(DatabaseOnlinePlayerObject.PARTY_ID.getName(), null);

        databaseManager.getCollection(databaseCollection).insertOne(document);
    }

    public void removeOnlinePlayer() {
        BasicDBObject dbObject = new BasicDBObject()
                .append(DatabaseOnlinePlayerObject.UUID.getName(), uuid);
        databaseManager.getCollection(databaseCollection).deleteOne(dbObject);
    }
}
