package de.gamechest.database.chatlog;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import de.gamechest.database.DatabaseCollection;
import de.gamechest.database.DatabaseManager;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ByteList on 11.04.2017.
 */
public class DatabaseChatlog {

    private final DatabaseManager databaseManager;
    private final DatabaseCollection databaseCollection = DatabaseCollection.CHAT_REPORTS;

    public DatabaseChatlog(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public boolean existsChatlog(String id) {
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find(Filters.eq(DatabaseChatlogObject.REPORT_ID.getName(), id));
        Document document = find.first();
        return document != null;
    }

    public boolean createChatlog(String id, String target, HashMap<String, ArrayList<Integer>> usersIds, HashMap<String, Long> timestamp, HashMap<String, String> prefixes, HashMap<String, String> messages) {
        if(existsChatlog(id)) return false;

        Document document = new Document();

        Document uIds = new Document();
        uIds.putAll(usersIds);

        Document timstp = new Document();
        timstp.putAll(timestamp);

        Document prfx = new Document();
        prfx.putAll(prefixes);

        Document msgs = new Document();
        msgs.putAll(messages);

        document.put(DatabaseChatlogObject.REPORT_ID.getName(), id);
        document.put(DatabaseChatlogObject.TYPE.getName(), "Player-Report");
        document.put(DatabaseChatlogObject.TARGET.getName(), target);
        document.put(DatabaseChatlogObject.USERS.getName(), uIds);
        document.put(DatabaseChatlogObject.TIMESTAMP.getName(), timstp);
        document.put(DatabaseChatlogObject.PREFIXES.getName(), prfx);
        document.put(DatabaseChatlogObject.MESSAGES.getName(), msgs);


        databaseManager.getCollection(databaseCollection).insertOne(document);
        return true;
    }

    public boolean createChatlog(String id,HashMap<String, ArrayList<Integer>> usersIds, HashMap<String, Long> timestamp, HashMap<String, String> prefixes, HashMap<String, String> messages) {
        if(existsChatlog(id)) return false;

        Document document = new Document();

        Document uIds = new Document();
        uIds.putAll(usersIds);

        Document timstp = new Document();
        timstp.putAll(timestamp);

        Document prfx = new Document();
        prfx.putAll(prefixes);

        Document msgs = new Document();
        msgs.putAll(messages);

        document.put(DatabaseChatlogObject.REPORT_ID.getName(), id);
        document.put(DatabaseChatlogObject.TYPE.getName(), "Serverlog");
        document.put(DatabaseChatlogObject.TARGET.getName(), null);
        document.put(DatabaseChatlogObject.USERS.getName(), uIds);
        document.put(DatabaseChatlogObject.TIMESTAMP.getName(), timstp);
        document.put(DatabaseChatlogObject.PREFIXES.getName(), prfx);
        document.put(DatabaseChatlogObject.MESSAGES.getName(), msgs);


        databaseManager.getCollection(databaseCollection).insertOne(document);
        return true;
    }

}
