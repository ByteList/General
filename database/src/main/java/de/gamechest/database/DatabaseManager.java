package de.gamechest.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.gamechest.common.database.ChestDatabaseManager;
import de.gamechest.database.activate.DatabaseActivate;
import de.gamechest.database.ban.DatabaseBan;
import de.gamechest.database.bug.DatabaseBugreport;
import de.gamechest.database.chatlog.DatabaseChatlog;
import de.gamechest.database.nick.DatabaseNick;
import de.gamechest.database.party.DatabaseParty;
import de.gamechest.database.poll.DatabasePoll;
import de.gamechest.database.premiumplayer.DatabasePremiumPlayer;
import de.gamechest.database.stats.clickattack.DatabaseClickAttack;
import de.gamechest.database.stats.deathrun.DatabaseDeathRun;
import de.gamechest.database.stats.jumpduell.DatabaseJumpDuell;
import de.gamechest.database.stats.network.DatabaseNetworkStats;
import de.gamechest.database.stats.shulkerdefence.DatabaseShulkerDefence;
import de.gamechest.database.terms.DatabaseTerms;
import de.gamechest.database.uuidbuffer.DatabaseUuidBuffer;
import de.gamechest.database.webregister.DatabaseWebRegister;
import lombok.Getter;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bson.codecs.UuidCodec;
import org.bson.codecs.configuration.CodecRegistries;

import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ByteList on 09.04.2017.
 *
 * Copyright by ByteList - https://bytelist.de/
 */
public class DatabaseManager implements ChestDatabaseManager {

    private HashMap<DatabaseCollection, MongoCollection<Document>> collections = new HashMap<>();

    @Getter
    private MongoClient mongoClient;
    @Getter
    private MongoDatabase mongoDatabase;

    @Getter
    private DatabaseClickAttack databaseClickAttack;
    @Getter
    private DatabaseShulkerDefence databaseShulkerDefence;
    @Getter
    private DatabaseDeathRun databaseDeathRun;
    @Getter
    private DatabaseJumpDuell databaseJumpDuell;
    @Getter
    private DatabaseChatlog databaseChatlog;
    @Getter
    private DatabaseNick databaseNick;
    @Getter
    private DatabasePremiumPlayer databasePremiumPlayer;
    @Getter
    private DatabaseBan databaseBan;
    @Getter
    private DatabaseUuidBuffer databaseUuidBuffer;
    @Getter
    private DatabaseBugreport databaseBugreport;
    @Getter
    private DatabaseActivate databaseActivate;
    @Getter
    private DatabaseParty databaseParty;
    @Getter
    private DatabasePoll databasePoll;
    @Getter
    private DatabaseWebRegister databaseWebRegister;
    @Getter
    private DatabaseTerms databaseTerms;
    @Getter
    private DatabaseNetworkStats databaseNetworkStats;

    @Getter
    private AsyncDatabaseManager async;

    public DatabaseManager(String host, int port, String username, String password, String database) {
        this(
                new MongoClient(
                        new ServerAddress(host, port),
                        Collections.singletonList(
                            MongoCredential.createCredential(username, database, password.toCharArray())
                        ),
                        MongoClientOptions.builder().codecRegistry(
                                CodecRegistries.fromRegistries(
                                        CodecRegistries.fromCodecs(new UuidCodec(UuidRepresentation.STANDARD)),
                                        MongoClient.getDefaultCodecRegistry()
                                )
                        ).build()
                ),
                database
        );
    }

    public DatabaseManager(MongoClient mongoClient, String database) {
        // Disable the stupid log messages from mongodb
        Logger mongoLog = Logger.getLogger("org.mongodb.driver");
        mongoLog.setLevel(Level.OFF);

        this.mongoClient = mongoClient;
        this.mongoDatabase = this.mongoClient.getDatabase(database);

        this.databaseClickAttack = new DatabaseClickAttack(this);
        this.databaseShulkerDefence = new DatabaseShulkerDefence(this);
        this.databaseDeathRun = new DatabaseDeathRun(this);
        this.databaseJumpDuell = new DatabaseJumpDuell(this);
        this.databaseChatlog = new DatabaseChatlog(this);
        this.databaseNick = new DatabaseNick(this);
        this.databasePremiumPlayer = new DatabasePremiumPlayer(this);
        this.databaseBan = new DatabaseBan(this);
        this.databaseUuidBuffer = new DatabaseUuidBuffer(this);
        this.databaseBugreport = new DatabaseBugreport(this);
        this.databaseActivate = new DatabaseActivate(this);
        this.databaseParty = new DatabaseParty(this);
        this.databasePoll = new DatabasePoll(this);
        this.databaseWebRegister = new DatabaseWebRegister(this);
        this.databaseTerms = new DatabaseTerms(this);
        this.databaseNetworkStats = new DatabaseNetworkStats(this);

        this.async = new AsyncDatabaseManager(this);
    }

    public void init() {
        for(DatabaseCollection col : DatabaseCollection.values()) {
            if (!existsCollection(col.getName())) mongoDatabase.createCollection(col.getName());
            collections.put(col, mongoDatabase.getCollection(col.getName()));
        }

    }

    public boolean existsCollection(String collection) {
        for(String cl : mongoDatabase.listCollectionNames()) {
            if(cl.equalsIgnoreCase(collection)) {
                return true;
            }
        }
        return false;
    }

    public MongoCollection<Document> getCollection(DatabaseCollection collectionEnum) {
        return collections.get(collectionEnum);
    }
}
