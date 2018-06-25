package de.gamechest.database.terms;

import lombok.Getter;

/**
 * Created by ByteList on 07.07.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public enum DatabaseTermsObject {

    UUID("UUID"),
    STATE("State");

    @Getter
    private String name;

    DatabaseTermsObject(String name) {
        this.name = name;
    }
}
