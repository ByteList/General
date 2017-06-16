package de.gamechest.database.bug;

import lombok.Getter;

/**
 * Created by ByteList on 11.04.2017.
 */
public enum DatabaseBugreportObject {

    BUG_ID("Bug-Id"),
    REASON("Reason"),
    SERVER_ID("Server-Id"),
    EXTRA_MESSAGE("Extra-Message"),
    STATE("State"),
    CREATED_BY("Created-By"),
    CREATE_DATE("Create-Date"),
    PREVIOUS_SERVER_ID("Previous-Server-Id");


    @Getter
    private String name;

    DatabaseBugreportObject(String name) {
        this.name = name;
    }

}
