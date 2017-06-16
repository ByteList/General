package de.gamechest.database.stats.shulkerdefence;

import lombok.Getter;

/**
 * Created by ByteList on 11.04.2017.
 */
public enum DatabaseShulkerDefenceObject {

    UUID("UUID"),
    RANK("Rank"),
    POINTS("Points"),
    KILLS("Kills"),
    DEATHS("Deaths"),
    GAMES("Games"),
    WINS("Wins"),
    KILLED_SHULKERS("Killed-Shulkers"),
    EARNED_COINS("Earned-Coins");


    @Getter
    private String name;

    DatabaseShulkerDefenceObject(String name) {
        this.name = name;
    }

}
