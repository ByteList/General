package de.gamechest.database.webregister;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import de.gamechest.database.DatabaseCollection;
import de.gamechest.database.DatabaseManager;
import org.bson.Document;

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
                .append(DatabaseWebRegisterObject.VERIFY_CODE.getName(), code);

        databaseManager.getCollection(databaseCollection).insertOne(document);
    }
}
