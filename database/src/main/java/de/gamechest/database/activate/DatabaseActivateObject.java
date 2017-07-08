package de.gamechest.database.activate;

import lombok.Getter;

/**
 * Created by ByteList on 07.07.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public enum DatabaseActivateObject {

    CODE("Code"),
    PURPOSE("Purpose"),
    VALUE("Value"),
    REDEEMER("Redeemer");

    @Getter
    private String name;

    DatabaseActivateObject(String name) {
        this.name = name;
    }
}
