package de.gamechest.database.stats.clickattack;

import lombok.Getter;

/**
 * Created by ByteList on 11.04.2017.
 */
public enum DatabaseClickAttackObject {

    UUID("UUID"),
    RANK("Rank"),
    POINTS("Points"),
    KILLS("Kills"),
    DEATHS("Deaths"),
    GAMES("Games"),
    WINS("Wins"),
    CLICKED_BLOCKS("Clicked-Blocks"),
    EARNED_COINS("Earned-Coins");


    @Getter
    private String name;

    DatabaseClickAttackObject(String name) {
        this.name = name;
    }

}
