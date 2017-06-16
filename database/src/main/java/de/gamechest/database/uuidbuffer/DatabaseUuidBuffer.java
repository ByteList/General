package de.gamechest.database.uuidbuffer;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import de.gamechest.database.DatabaseCollection;
import de.gamechest.database.DatabaseManager;
import org.bson.Document;

import java.util.UUID;

/**
 * Created by ByteList on 11.04.2017.
 */
public class DatabaseUuidBuffer {

    private final DatabaseManager databaseManager;
    private final DatabaseCollection databaseCollection = DatabaseCollection.UUID_BUFFER;

    public DatabaseUuidBuffer(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public boolean existsPlayer(String name) {
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find(Filters.eq(DatabaseUuidBufferObject.NAME.getName(), name));
        Document document = find.first();
        return document != null;
    }

    public void createPlayer(String name, UUID uuid) {
        if(existsPlayer(name)) removePlayer(name);

        Document document = new Document()
                .append(DatabaseUuidBufferObject.NAME.getName(), name)
                .append(DatabaseUuidBufferObject.UUID.getName(), uuid.toString());

        databaseManager.getCollection(databaseCollection).insertOne(document);
    }

    public void removePlayer(String name) {
        BasicDBObject dbObject = new BasicDBObject()
                .append(DatabaseUuidBufferObject.NAME.getName(), name);
        databaseManager.getCollection(databaseCollection).deleteOne(dbObject);
    }

    public UUID getUUID(String name) {
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find(Filters.eq(DatabaseUuidBufferObject.NAME.getName(), name));
        Document document = find.first();

        if(document == null) return null;

        return UUID.fromString(document.get(DatabaseUuidBufferObject.UUID.getName()).toString());
    }
}