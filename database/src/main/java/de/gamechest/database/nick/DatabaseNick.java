package de.gamechest.database.nick;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import de.gamechest.database.DatabaseCollection;
import de.gamechest.database.DatabaseElement;
import de.gamechest.database.DatabaseManager;
import org.bson.Document;

import java.util.Random;

/**
 * Created by ByteList on 11.04.2017.
 */
public class DatabaseNick {

    private final Random random = new Random();
    private final DatabaseManager databaseManager;
    private final DatabaseCollection databaseCollection = DatabaseCollection.NICKNAMES;

    private final int NICK_SIZE;

    public DatabaseNick(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.NICK_SIZE = 64;
    }

    private int TRIES = 0;

    public String getRandomNickname() {
        int id = random.nextInt(NICK_SIZE);
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find(Filters.eq(DatabaseNickObject.SORT_ID.getName(), id));
        Document document = find.first();

        Object used = document.get(DatabaseNickObject.USED.getName());

        try {
            boolean b = Boolean.parseBoolean(used.toString());
        } catch (Exception ex) {
            if (TRIES < 5) {
                TRIES++;
                return getRandomNickname();
            }
        }

        TRIES = 0;
        return document.getString(DatabaseNickObject.NICK.getName());
    }

    public void setDatabaseObject(String nick, DatabaseNickObject databaseNickObject, Object value) {
        this.databaseManager.getAsync().getExecutor().execute(() -> {
            BasicDBObject doc = new BasicDBObject();
            doc.append("$set", new BasicDBObject().append(databaseNickObject.getName(), value));

            BasicDBObject basicDBObject = new BasicDBObject().append(DatabaseNickObject.NICK.getName(), nick);
            databaseManager.getCollection(databaseCollection).updateOne(basicDBObject, doc);
        });
    }

    public void createNick(int id, String nick, String value, String signature) {
        this.databaseManager.getAsync().getExecutor().execute(() -> {

            Document skinTexture = new Document();
            skinTexture.put(DatabaseNickObject.SkinObject.VALUE.getName(), value);
            skinTexture.put(DatabaseNickObject.SkinObject.SIGNATURE.getName(), signature);

            Document document = new Document();
            document.put(DatabaseNickObject.SORT_ID.getName(), id);
            document.put(DatabaseNickObject.NICK.getName(), nick);
            document.put(DatabaseNickObject.USED.getName(), false);
            document.put(DatabaseNickObject.SKIN_TEXTURE.getName(), skinTexture);

            databaseManager.getCollection(databaseCollection).insertOne(document);
        });
    }

    public DatabaseElement getDatabaseElement(DatabaseNickObject where, String whereValue, DatabaseNickObject databaseNickObject) {
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find(Filters.eq(where.getName(), whereValue));
        Document document = find.first();

        if (document == null) return null;

        return new DatabaseElement(document.get(databaseNickObject.getName()));
    }
}
