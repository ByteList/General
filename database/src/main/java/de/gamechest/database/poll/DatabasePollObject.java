package de.gamechest.database.poll;

import lombok.Getter;

/**
 * Created by ByteList on 07.07.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public enum DatabasePollObject {

    POLL("Poll"),
    OPENED("Opened"),
    POSSIBILITIES("Possibilities"),
    VOTED_USER("Voted-User"),
    END("End");

    @Getter
    private String name;

    DatabasePollObject(String name) {
        this.name = name;
    }
}
