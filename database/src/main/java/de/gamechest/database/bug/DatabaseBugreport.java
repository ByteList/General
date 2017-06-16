package de.gamechest.database.bug;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import de.gamechest.database.DatabaseCollection;
import de.gamechest.database.DatabaseElement;
import de.gamechest.database.DatabaseManager;
import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * Created by ByteList on 25.04.2017.
 */
public class DatabaseBugreport {

    private final DatabaseManager databaseManager;
    private final DatabaseCollection databaseCollection = DatabaseCollection.BUG_REPORTS;

    public DatabaseBugreport(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void createBugreport(String bugId, BugReason bugReason, String serverId, String extra, UUID uuid,  String previousServerId) {
        if(existsBugreport(bugId)) return;
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        Document document = new Document()
                .append(DatabaseBugreportObject.BUG_ID.getName(), bugId)
                .append(DatabaseBugreportObject.REASON.getName(), bugReason.toString())
                .append(DatabaseBugreportObject.SERVER_ID.getName(), serverId)
                .append(DatabaseBugreportObject.EXTRA_MESSAGE.getName(), extra)
                .append(DatabaseBugreportObject.STATE.getName(), BugState.WAITING.toString())
                .append(DatabaseBugreportObject.CREATED_BY.getName(), uuid.toString())
                .append(DatabaseBugreportObject.CREATE_DATE.getName(), formatter.format(Calendar.getInstance().getTime()))
                .append(DatabaseBugreportObject.PREVIOUS_SERVER_ID.getName(), previousServerId);

        databaseManager.getCollection(databaseCollection).insertOne(document);
    }

    public boolean existsBugreport(String bugId) {
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find(Filters.eq(DatabaseBugreportObject.BUG_ID.getName(), bugId));
        Document document = find.first();
        return document != null;
    }

    public void setDatabaseObject(String bugId, DatabaseBugreportObject databaseBugreportObject, Object value) {
        BasicDBObject doc = new BasicDBObject();
        doc.append("$set", new BasicDBObject().append(databaseBugreportObject.getName(), value));

        BasicDBObject basicDBObject = new BasicDBObject().append(DatabaseBugreportObject.BUG_ID.getName(), bugId);
        databaseManager.getCollection(databaseCollection).updateOne(basicDBObject, doc);
    }

    public DatabaseElement getDatabaseElement(String bugId, DatabaseBugreportObject databaseBugreportObject) {
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find(Filters.eq(DatabaseBugreportObject.BUG_ID.getName(), bugId));
        Document document = find.first();

        if(document == null) return null;

        return new DatabaseElement(document.get(databaseBugreportObject.getName()));
    }

    public List<String> getBugreportIds() {
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find();
        List<String> list = new ArrayList<>();
        for(Document document : find) {
            list.add(document.getString(DatabaseBugreportObject.BUG_ID.getName()));
        }
        return list;
    }

    public List<String> getWaitingReports() {
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find(Filters.eq(DatabaseBugreportObject.STATE.getName(), BugState.WAITING.toString()));
        List<String> list = new ArrayList<>();
        for(Document document : find) {
            list.add(document.getString(DatabaseBugreportObject.BUG_ID.getName()));
        }
        return list;
    }

    public long getReportedBugs() {
        return databaseManager.getCollection(databaseCollection).count();
    }
}
