package de.gamechest.database.poll;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import de.gamechest.database.DatabaseCollection;
import de.gamechest.database.DatabaseElement;
import de.gamechest.database.DatabaseManager;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by ByteList on 07.07.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class DatabasePoll {

    private final DatabaseManager databaseManager;
    private final DatabaseCollection databaseCollection = DatabaseCollection.POLL;

    public DatabasePoll(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void setPoll(String poll, HashMap<String, Integer> possibilities, String end) {
        this.databaseManager.getAsync().getExecutor().execute(() -> {
            if (existsPoll()) {
                if (isPollOpened())
                    return;
                BasicDBObject dbObject = new BasicDBObject().append("_poll", "poll");
                databaseManager.getCollection(databaseCollection).deleteOne(dbObject);
            }

            Document document = new Document()
                    .append("_poll", "p")
                    .append(DatabasePollObject.POLL.getName(), poll)
                    .append(DatabasePollObject.OPENED.getName(), false)
                    .append(DatabasePollObject.POSSIBILITIES.getName(), possibilities)
                    .append(DatabasePollObject.VOTED_USER.getName(), new ArrayList<String>())
                    .append(DatabasePollObject.END.getName(), end);

            databaseManager.getCollection(databaseCollection).insertOne(document);
        });
    }

    public boolean existsPoll() {
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find(Filters.eq("_poll", "poll"));
        Document document = find.first();
        return document != null;
    }

    public boolean isPollOpened() {
        return getDatabaseElement(DatabasePollObject.OPENED).getAsBoolean();
    }

    public void closePoll() {
        setDatabaseObject(DatabasePollObject.OPENED, false);
    }

    public void vote(UUID uuid, String possibility) {
        this.databaseManager.getAsync().getExecutor().execute(() -> {
            if (!canVote(uuid)) {
                return;
            }
            HashMap<String, Integer> pos = getPossibilities();
            ArrayList<String> voted = getVotes();

            pos.put(possibility, pos.get(possibility) + 1);
            voted.add(uuid.toString());
            setDatabaseObject(DatabasePollObject.POSSIBILITIES, pos);
            setDatabaseObject(DatabasePollObject.VOTED_USER, voted);
        });
    }

    public boolean canVote(UUID uuid) {
        return getVotes().contains(uuid.toString());
    }

    public ArrayList<String> getVotes() {
        return (ArrayList<String>) getDatabaseElement(DatabasePollObject.VOTED_USER).getObject();
    }

    public HashMap<String, Integer> getPossibilities() {
        return (HashMap<String, Integer>) getDatabaseElement(DatabasePollObject.POSSIBILITIES).getObject();
    }

    public void setDatabaseObject(DatabasePollObject databasePollObject, Object value) {
        this.databaseManager.getAsync().getExecutor().execute(() -> {
            BasicDBObject doc = new BasicDBObject();
            doc.append("$set", new BasicDBObject().append(databasePollObject.getName(), value));

            BasicDBObject basicDBObject = new BasicDBObject().append("_poll", "poll");
            databaseManager.getCollection(databaseCollection).updateOne(basicDBObject, doc);
        });
    }

    public DatabaseElement getDatabaseElement(DatabasePollObject databasePollObject) {
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find(Filters.eq("_poll", "poll"));
        Document document = find.first();

        if (document == null) return null;

        return new DatabaseElement(document.get(databasePollObject.getName()));
    }
}
