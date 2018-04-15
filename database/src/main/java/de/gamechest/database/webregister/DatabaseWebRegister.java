package de.gamechest.database.webregister;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import de.gamechest.database.DatabaseCollection;
import de.gamechest.database.DatabaseElement;
import de.gamechest.database.DatabaseManager;
import org.bson.Document;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by ByteList on 11.04.2017.
 */
public class DatabaseWebRegister {

    private final DatabaseManager databaseManager;
    private final DatabaseCollection databaseCollection = DatabaseCollection.WEB_REGISTER;

    public DatabaseWebRegister(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public boolean existsUser(UUID uuid) {
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find(Filters.eq(DatabaseWebRegisterObject.UUID.getName(), uuid.toString()));
        Document document = find.first();
        return document != null;
    }

    public void register(UUID uuid, String mail, String code) {
        if (existsUser(uuid)) return;
        Document document = new Document()
                .append(DatabaseWebRegisterObject.UUID.getName(), uuid.toString())
                .append(DatabaseWebRegisterObject.MAIL_ADDRESS.getName(), mail)
                .append(DatabaseWebRegisterObject.VERIFY_CODE.getName(), code)
                .append(DatabaseWebRegisterObject.STATE.getName(), "0");

        databaseManager.getCollection(databaseCollection).insertOne(document);
    }

    public ArrayList<Document> getAllByState(String state) {
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection)
                .find(Filters.eq(DatabaseWebRegisterObject.STATE.getName(), state));

        ArrayList<Document> documents = new ArrayList<>();
        find.forEach((Block<? super Document>) documents::add);

        return documents;
    }

    public void setDatabaseObject(UUID uuid, DatabaseWebRegisterObject databaseWebRegisterObject, Object value) {
        BasicDBObject doc = new BasicDBObject();
        doc.append("$set", new BasicDBObject().append(databaseWebRegisterObject.getName(), value));

        BasicDBObject basicDBObject = new BasicDBObject().append(DatabaseWebRegisterObject.UUID.getName(), uuid.toString());
        databaseManager.getCollection(databaseCollection).updateOne(basicDBObject, doc);
    }

    public DatabaseElement getDatabaseElement(UUID uuid, DatabaseWebRegisterObject databaseWebRegisterObject) {
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find(Filters.eq(DatabaseWebRegisterObject.UUID.getName(), uuid));
        Document document = find.first();

        if (document == null) return null;

        return new DatabaseElement(document.get(databaseWebRegisterObject.getName()));
    }
}
