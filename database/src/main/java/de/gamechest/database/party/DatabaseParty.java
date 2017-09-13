package de.gamechest.database.party;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import de.gamechest.database.DatabaseCollection;
import de.gamechest.database.DatabaseElement;
import de.gamechest.database.DatabaseManager;
import org.bson.Document;
import org.json.simple.JSONArray;

import java.util.ArrayList;

/**
 * Created by ByteList on 15.04.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class DatabaseParty {

    private final DatabaseManager databaseManager;
    private final DatabaseCollection databaseCollection = DatabaseCollection.PARTY;

    public DatabaseParty(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void createParty(String partyId, String leader) {
        this.databaseManager.getAsync().getExecutor().execute(() -> {
            Document document = new Document()
                    .append(DatabasePartyObject.PARTY_ID.getName(), partyId)
                    .append(DatabasePartyObject.LEADER.getName(), leader)
                    .append(DatabasePartyObject.MEMBERS.getName(), new JSONArray());

            databaseManager.getCollection(databaseCollection).insertOne(document);
        });
    }

    public boolean addMember(String partyId, String member) {
        ArrayList<String> members = getMembers(partyId);
        if (!members.contains(member)) {
            members.add(member);
            setDatabaseObject(partyId, DatabasePartyObject.MEMBERS, members);
            return true;
        }
        return false;
    }

    public boolean removeMember(String partyId, String member) {
        ArrayList<String> members = getMembers(partyId);
        if (members.contains(member)) {
            members.remove(member);
            setDatabaseObject(partyId, DatabasePartyObject.MEMBERS, members);
            return true;
        }
        return false;
    }

    public void setLeader(String partyId, String leader) {
        this.databaseManager.getAsync().getExecutor().execute(() -> {
            setDatabaseObject(partyId, DatabasePartyObject.LEADER, leader);
        });
    }

    public void deleteParty(String partyId) {
        this.databaseManager.getAsync().getExecutor().execute(() -> {
            BasicDBObject dbObject = new BasicDBObject()
                    .append(DatabasePartyObject.PARTY_ID.getName(), partyId);
            databaseManager.getCollection(databaseCollection).deleteOne(dbObject);
        });
    }

    public ArrayList<String> getMembers(String partyId) {
        DatabaseElement databaseElement = getDatabaseElement(partyId, DatabasePartyObject.MEMBERS);
        if (databaseElement != null)
            return (ArrayList<String>) databaseElement.getObject();
        return new ArrayList<>();
    }

    @SuppressWarnings("ConstantConditions")
    public String getLeader(String partyId) {
        if (getDatabaseElement(partyId, DatabasePartyObject.LEADER).getObject() == null)
            return null;
        else return getDatabaseElement(partyId, DatabasePartyObject.LEADER).getAsString();
    }

    private void setDatabaseObject(String partyId, DatabasePartyObject databasePartyObject, Object value) {
        this.databaseManager.getAsync().getExecutor().execute(() -> {
            BasicDBObject doc = new BasicDBObject();
            doc.append("$set", new BasicDBObject().append(databasePartyObject.getName(), value));

            BasicDBObject basicDBObject = new BasicDBObject().append(DatabasePartyObject.PARTY_ID.getName(), partyId);
            databaseManager.getCollection(databaseCollection).updateOne(basicDBObject, doc);
        });
    }

    private DatabaseElement getDatabaseElement(String partyId, DatabasePartyObject databasePartyObject) {
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find(Filters.eq(DatabasePartyObject.PARTY_ID.getName(), partyId));
        Document document = find.first();

        if (document == null) return null;

        return new DatabaseElement(document.get(databasePartyObject.getName()));
    }
}
