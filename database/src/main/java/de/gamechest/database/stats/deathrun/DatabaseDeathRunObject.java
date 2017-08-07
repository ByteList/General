package de.gamechest.database.stats.deathrun;

import lombok.Getter;

/**
 * Created by ByteList on 11.04.2017.
 */
public enum DatabaseDeathRunObject {

    UUID("UUID"),
    RANK("Rank"),
    POINTS("Points"),
    DEATHS("Deaths"),
    GAMES("Games"),
    WINS("Wins"),
    EARNED_COINS("Earned-Coins"),
    USED_DOUBLE_JUMPS("Used-Double-Jumps"),
    USED_ITEMS("Used-Items");

    @Getter
    private String name;

    DatabaseDeathRunObject(String name) {
        this.name = name;
    }

}
