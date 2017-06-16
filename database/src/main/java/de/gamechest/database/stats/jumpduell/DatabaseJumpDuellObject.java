package de.gamechest.database.stats.jumpduell;

import lombok.Getter;

/**
 * Created by ByteList on 11.04.2017.
 */
public enum DatabaseJumpDuellObject {

    UUID("UUID"),
    RANK("Rank"),
    POINTS("Points"),
    KILLS("Kills"),
    DEATHS("Deaths"),
    GAMES("Games"),
    WINS("Wins"),
    FAILS("Fails"),
    ONE_DUELL("One-Duell"),
    TRIPLE_DUELL("Triple-Duell"),
    ALONE("Alone"),
    EARNED_COINS("Earned-Coins");

    @Getter
    private String name;

    DatabaseJumpDuellObject(String name) {
        this.name = name;
    }

}
