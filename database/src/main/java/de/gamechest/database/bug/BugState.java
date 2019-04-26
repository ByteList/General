package de.gamechest.database.bug;

import lombok.Getter;

import java.util.ArrayList;

/**
 * Created by ByteList on 25.04.2017.
 */
public enum BugState {

    WAITING("§cWaiting"),
    SEEN("§6Seen"),
    NO_BUG("§cNo Bug!"),
    FIXED("§2Fixed!");

    @Getter
    private String betterString;

    BugState(String betterString) {
        this.betterString = betterString;
    }


    public static ArrayList<String> getBugStateAsString() {
        ArrayList<String> units = new ArrayList<>();
        for (BugState unit : values()) {
            units.add(unit.toString());
        }
        return units;
    }

    public static BugState getBugState(String bugState) {
        for (BugState units : values()) {
            if (units.toString().toUpperCase().equals(bugState.toUpperCase())) {
                return units;
            }
        }
        throw new IllegalArgumentException(bugState + " is not a better bug reason!");
    }

}
