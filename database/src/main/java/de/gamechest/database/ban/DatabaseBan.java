package de.gamechest.database.ban;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import de.gamechest.database.DatabaseCollection;
import de.gamechest.database.DatabaseElement;
import de.gamechest.database.DatabaseManager;
import de.gamechest.database.DatabasePlayerObject;
import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * Created by ByteList on 15.04.2017.
 */
public class DatabaseBan {

    private final DatabaseManager databaseManager;
    private final DatabaseCollection databaseCollection = DatabaseCollection.BANS;

    public DatabaseBan(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void ban(UUID uuid, Reason reason, String addon, String onlyStaff, String sender) {
        if (isBanned(uuid)) unBan(uuid);
        this.databaseManager.getAsync().getExecutor().execute(() -> {

            Integer time = reason.getTime();
            String value = reason.getValue().getShortcut();
            String end;
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");

            if (time == -1) {
                end = "-1";
            } else {
                Calendar now = Calendar.getInstance();

                if (value.equalsIgnoreCase("d")) {
                    now.add(5, time);
                } else if (value.equalsIgnoreCase("h")) {
                    now.add(11, time);
                } else if (value.equalsIgnoreCase("min")) {
                    now.add(12, time);
                } else if (value.equalsIgnoreCase("s")) {
                    now.add(13, time);
                } else if (value.equalsIgnoreCase("m")) {
                    now.add(2, time);
                } else if (value.equalsIgnoreCase("y")) {
                    now.add(1, time);
                } else if (value.equalsIgnoreCase("w")) {
                    now.add(4, time);
                }
                end = formatter.format(now.getTime());
            }

            Document document = new Document()
                    .append(DatabaseBanObject.UUID.getName(), uuid.toString())
                    .append(DatabaseBanObject.START_DATE.getName(), formatter.format(Calendar.getInstance().getTime()))
                    .append(DatabaseBanObject.END_DATE.getName(), end)
                    .append(DatabaseBanObject.REASON.getName(), reason.toString())
                    .append(DatabaseBanObject.SENDER.getName(), sender)
                    .append(DatabaseBanObject.EXTRA_MESSAGE.getName(), addon)
                    .append(DatabaseBanObject.STAFF_ONLY.getName(), onlyStaff);

            databaseManager.getCollection(databaseCollection).insertOne(document);
        });
    }

    public void unBan(UUID uuid) {
        this.databaseManager.getAsync().getExecutor().execute(() -> {
            BasicDBObject dbObject = new BasicDBObject()
                    .append(DatabaseBanObject.UUID.getName(), uuid.toString());
            databaseManager.getCollection(databaseCollection).deleteOne(dbObject);
        });
    }

    public boolean isBanned(UUID uuid) {
        String uid = uuid.toString();
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find(Filters.eq(DatabaseBanObject.UUID.getName(), uid));
        Document document = find.first();
        return document != null;
    }

    public void setDatabaseObject(UUID uuid, DatabaseBanObject databaseBanObject, Object value) {
        this.databaseManager.getAsync().getExecutor().execute(() -> {
            String uid = uuid.toString();
            BasicDBObject doc = new BasicDBObject();
            doc.append("$set", new BasicDBObject().append(databaseBanObject.getName(), value));

            BasicDBObject basicDBObject = new BasicDBObject().append(DatabasePlayerObject.UUID.getName(), uid);
            databaseManager.getCollection(databaseCollection).updateOne(basicDBObject, doc);
        });
    }

    public DatabaseElement getDatabaseElement(UUID uuid, DatabaseBanObject databaseBanObject) {
        String uid = uuid.toString();
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find(Filters.eq(DatabasePlayerObject.UUID.getName(), uid));
        Document document = find.first();

        if (document == null) return null;

        return new DatabaseElement(document.get(databaseBanObject.getName()));
    }

    public List<UUID> getBannedUuids() {
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find();
        List<UUID> list = new ArrayList<>();
        for (Document document : find) {
            list.add(UUID.fromString(document.getString(DatabaseBanObject.UUID.getName())));
        }
        return list;
    }
}
