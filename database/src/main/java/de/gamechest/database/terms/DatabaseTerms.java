package de.gamechest.database.terms;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import de.gamechest.database.DatabaseCollection;
import de.gamechest.database.DatabaseElement;
import de.gamechest.database.DatabaseManager;
import org.bson.Document;

import java.util.UUID;

/**
 * Created by ByteList on 07.07.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class DatabaseTerms {

    private final DatabaseManager databaseManager;
    private final DatabaseCollection databaseCollection = DatabaseCollection.TERMS;

    public DatabaseTerms(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void createPlayer(UUID uuid) {
        if (existsPlayer(uuid)) return;
        this.databaseManager.getAsync().getExecutor().execute(() -> {

            Document document = new Document()
                    .append(DatabaseTermsObject.UUID.getName(), uuid.toString())
                    .append(DatabaseTermsObject.STATE.getName(), 0);

            databaseManager.getCollection(databaseCollection).insertOne(document);
        });
    }

    public boolean existsPlayer(UUID uuid) {
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find(Filters.eq(DatabaseTermsObject.UUID.getName(), uuid.toString()));
        Document document = find.first();
        return document != null;
    }

    public void setDatabaseObject(UUID uuid, DatabaseTermsObject databaseTermsObject, Object value) {
        this.databaseManager.getAsync().getExecutor().execute(() -> {
            BasicDBObject doc = new BasicDBObject();
            doc.append("$set", new BasicDBObject().append(databaseTermsObject.getName(), value));

            BasicDBObject basicDBObject = new BasicDBObject().append(DatabaseTermsObject.UUID.getName(), uuid.toString());
            databaseManager.getCollection(databaseCollection).updateOne(basicDBObject, doc);
        });
    }

    public DatabaseElement getDatabaseElement(UUID uuid, DatabaseTermsObject databaseTermsObject) {
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find(Filters.eq(DatabaseTermsObject.UUID.getName(), uuid.toString()));
        Document document = find.first();

        if (document == null) return null;

        return new DatabaseElement(document.get(databaseTermsObject.getName()));
    }

}
