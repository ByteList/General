package de.gamechest.database.ban;

import lombok.Getter;

/**
 * Created by ByteList on 15.04.2017.
 */
public enum DatabaseBanObject {

    UUID("UUID"),
    START_DATE("Start-Date"),
    END_DATE("End-Date"),
    REASON("Reason"),
    SENDER("Sender"),
    EXTRA_MESSAGE("Extra-Message"),
    STAFF_ONLY("Staff-Only");

    @Getter
    private String name;

    DatabaseBanObject(String name) {
        this.name = name;
    }
}
