package de.gamechest.database.stats.deathrun;

import lombok.Getter;

/**
 * Created by ByteList on 11.04.2017.
 */
public enum DatabaseDeathRunObject {

    UUID("UUID"),
    RANK("Rank"),
    POINTS("Points"),
    KILLS("Kills"),
    DEATHS("Deaths"),
    GAMES("Games"),
    WINS("Wins"),
    EARNED_COINS("Earned-Coins");

    @Getter
    private String name;

    DatabaseDeathRunObject(String name) {
        this.name = name;
    }

}
