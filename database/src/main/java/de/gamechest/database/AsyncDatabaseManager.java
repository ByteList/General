package de.gamechest.database;

import de.gamechest.database.onlineplayer.DatabaseOnlinePlayer;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayerObject;
import de.gamechest.database.webregister.DatabaseWebRegister;
import lombok.Getter;

import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by ByteList on 05.07.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class AsyncDatabaseManager {

    private final DatabaseManager databaseManager;

    @Getter
    private Executor executor;

    public AsyncDatabaseManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.executor = Executors.newCachedThreadPool();
    }

    public void getPlayer(UUID uuid, Callback<DatabasePlayer> callback) {
        this.executor.execute(()->
                callback.run(new DatabasePlayer(databaseManager, uuid)));
    }

    public void getPlayer(UUID uuid, Callback<DatabasePlayer> callback, DatabasePlayerObject... accesses) {
        this.executor.execute(()->
                callback.run(new DatabasePlayer(databaseManager, uuid, accesses)));
    }

    public void getPlayerByName(String name, Callback<DatabasePlayer> callback) {
        this.executor.execute(()->
            callback.run(new DatabasePlayer(databaseManager, this.databaseManager.getDatabaseUuidBuffer().getUUID(name))));
    }

    public void getPlayerByName(String name, Callback<DatabasePlayer> callback, DatabasePlayerObject... accesses) {
        this.executor.execute(()->
                callback.run(new DatabasePlayer(databaseManager, this.databaseManager.getDatabaseUuidBuffer().getUUID(name), accesses)));
    }

    public void getOnlinePlayer(UUID uuid, Callback<DatabaseOnlinePlayer> callback) {
        this.executor.execute(()-> callback.run(new DatabaseOnlinePlayer(databaseManager, uuid.toString(), null)));
    }

    public void getOnlinePlayer(UUID uuid, String name, Callback<DatabaseOnlinePlayer> callback) {
        this.executor.execute(()-> callback.run(new DatabaseOnlinePlayer(databaseManager, uuid.toString(), name)));
    }

    public void getOnlinePlayer(UUID uuid, Callback<DatabaseOnlinePlayer> callback, DatabaseOnlinePlayerObject... accesses) {
        this.executor.execute(()-> callback.run(new DatabaseOnlinePlayer(databaseManager, uuid.toString(), null, accesses)));
    }

    public void getWebRegister(Callback<DatabaseWebRegister> callback) {
        this.executor.execute(()-> callback.run(this.databaseManager.getDatabaseWebRegister()));
    }
}