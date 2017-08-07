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
    NICKNAME("Nickname"),
    PARTY_ID("Party-Id");

    public static String[] toStringList(DatabaseOnlinePlayerObject ... rules) {
        String[] list = new String[rules.length];
        for (int i = 0; i < rules.length; i++)
            list[i] = rules[i].getName();
        return list;
    }

    @Getter
    private String name;

    DatabaseOnlinePlayerObject(String name) {
        this.name = name;
    }

}
