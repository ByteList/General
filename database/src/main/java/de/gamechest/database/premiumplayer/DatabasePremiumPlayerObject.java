package de.gamechest.database.premiumplayer;

import lombok.Getter;

/**
 * Created by ByteList on 11.04.2017.
 */
public enum DatabasePremiumPlayerObject {

    UUID("UUID"),
    ENDING_DATE("Ending-Date");

    @Getter
    private String name;

    DatabasePremiumPlayerObject(String name) {
        this.name = name;
    }

}
