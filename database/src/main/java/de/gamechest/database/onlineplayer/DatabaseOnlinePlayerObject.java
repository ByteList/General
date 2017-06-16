package de.gamechest.database.onlineplayer;

import lombok.Getter;

/**
 * Created by ByteList on 11.04.2017.
 */
public enum DatabaseOnlinePlayerObject {

    UUID("UUID"),
    NAME("Name"),
    SERVER_ID("Server-Id"),
    PREVIOUS_SERVER_ID("Previous-Server-Id"),
    TOGGLED_RANK("Toggled-Rank"),
    NICKNAME("Nickname"),
    PARTY_ID("Party-Id");


    @Getter
    private String name;

    DatabaseOnlinePlayerObject(String name) {
        this.name = name;
    }

}
