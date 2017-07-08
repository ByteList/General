package de.gamechest.coins;

import de.gamechest.GameChest;
import de.gamechest.database.DatabaseManager;
import de.gamechest.database.DatabasePlayer;
import de.gamechest.database.DatabasePlayerObject;

import java.util.UUID;

/**
 * Created by ByteList on 06.07.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class Coins {

    private final DatabaseManager databaseManager;

    public Coins() {
        this.databaseManager = GameChest.getInstance().getDatabaseManager();
    }

    public long getCoins(UUID uuid) {
        return new DatabasePlayer(databaseManager, uuid).getDatabaseElement(DatabasePlayerObject.COINS).getAsLong();
    }

    public void setCoins(UUID uuid, long coins) {
        databaseManager.getAsync().getPlayer(uuid, dbPlayer -> dbPlayer.setDatabaseObject(DatabasePlayerObject.COINS, coins));
    }

    public void addCoins(UUID uuid, long coins) {
        databaseManager.getAsync().getPlayer(uuid, dbPlayer -> dbPlayer.setDatabaseObject(DatabasePlayerObject.COINS, dbPlayer.getDatabaseElement(DatabasePlayerObject.COINS).getAsLong()+coins));
    }

    public void removeCoins(UUID uuid, long coins) {
        databaseManager.getAsync().getPlayer(uuid, dbPlayer -> dbPlayer.setDatabaseObject(DatabasePlayerObject.COINS, dbPlayer.getDatabaseElement(DatabasePlayerObject.COINS).getAsLong()-coins));
    }
}
