package de.gamechest.database.activate;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import de.gamechest.database.DatabaseCollection;
import de.gamechest.database.DatabaseElement;
import de.gamechest.database.DatabaseManager;
import org.bson.Document;

/**
 * Created by ByteList on 07.07.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class DatabaseActivate {

    private final DatabaseManager databaseManager;
    private final DatabaseCollection databaseCollection = DatabaseCollection.ACTIVATE_CODES;

    public DatabaseActivate(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void createCode(String code, ActivatePurpose purpose, Object value) {
        if(existsCode(code)) return;

        Document document = new Document()
                .append(DatabaseActivateObject.CODE.getName(), code)
                .append(DatabaseActivateObject.PURPOSE.getName(), purpose.toString())
                .append(DatabaseActivateObject.VALUE.getName(), value)
                .append(DatabaseActivateObject.REDEEMER.getName(), null);

        databaseManager.getCollection(databaseCollection).insertOne(document);
    }

    public boolean existsCode(String code) {
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find(Filters.eq(DatabaseActivateObject.CODE.getName(), code));
        Document document = find.first();
        return document != null;
    }

    public void setDatabaseObject(String code, DatabaseActivateObject databaseActivateObject, Object value) {
        BasicDBObject doc = new BasicDBObject();
        doc.append("$set", new BasicDBObject().append(databaseActivateObject.getName(), value));

        BasicDBObject basicDBObject = new BasicDBObject().append(DatabaseActivateObject.CODE.getName(), code);
        databaseManager.getCollection(databaseCollection).updateOne(basicDBObject, doc);
    }

    public DatabaseElement getDatabaseElement(String code, DatabaseActivateObject databaseActivateObject) {
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find(Filters.eq(DatabaseActivateObject.CODE.getName(), code));
        Document document = find.first();

        if(document == null) return null;

        return new DatabaseElement(document.get(databaseActivateObject.getName()));
    }

    public enum ActivatePurpose {
        PREMIUM,
        COINS
    }
}
