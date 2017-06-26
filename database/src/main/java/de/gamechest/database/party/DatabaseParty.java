package de.gamechest.database.party;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import de.gamechest.database.DatabaseCollection;
import de.gamechest.database.DatabaseElement;
import de.gamechest.database.DatabaseManager;
import de.gamechest.database.DatabasePlayerObject;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by ByteList on 15.04.2017.
 */
public class DatabaseParty {

    private final DatabaseManager databaseManager;
    private final DatabaseCollection databaseCollection = DatabaseCollection.PARTY;

    public DatabaseParty(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void createParty(String partyId, String leader) {
        Document document = new Document()
                .append(DatabasePartyObject.PARTY_ID.getName(), partyId)
                .append(DatabasePartyObject.LEADER.getName(), leader)
                .append(DatabasePartyObject.MEMBERS.getName(), new Document());

        databaseManager.getCollection(databaseCollection).insertOne(document);
    }

    public boolean addMember(String partyId, String member) {
        ArrayList<String> members = getMembers(partyId);
        members.append()
    }

    public boolean removeMember() {

    }

    public boolean setLeader() {

    }

    public boolean deleteParty() {

    }

    public ArrayList<String> getMembers(String partyId) {
        DatabaseElement databaseElement = getDatabaseElement(partyId, DatabasePartyObject.MEMBERS);
        if(databaseElement != null)
            return (ArrayList<String>) databaseElement.getObject();
        return new ArrayList<>();
    }

    private void setDatabaseObject(String partyId, DatabasePartyObject databasePartyObject, Object value) {
        BasicDBObject doc = new BasicDBObject();
        doc.append("$set", new BasicDBObject().append(databasePartyObject.getName(), value));

        BasicDBObject basicDBObject = new BasicDBObject().append(DatabasePartyObject.PARTY_ID.getName(), partyId);
        databaseManager.getCollection(databaseCollection).updateOne(basicDBObject, doc);
    }

    private DatabaseElement getDatabaseElement(String partyId, DatabasePartyObject databasePartyObject) {
        FindIterable<Document> find = databaseManager.getCollection(databaseCollection).find(Filters.eq(DatabasePartyObject.PARTY_ID.getName(), partyId));
        Document document = find.first();

        if(document == null) return null;

        return new DatabaseElement(document.get(databasePartyObject.getName()));
    }
}
