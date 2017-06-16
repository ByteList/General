package de.gamechest.database.uuidbuffer;

import lombok.Getter;

/**
 * Created by ByteList on 11.04.2017.
 */
public enum DatabaseUuidBufferObject {

    NAME("Name"),
    UUID("UUID");

    @Getter
    private String name;

    DatabaseUuidBufferObject(String name) {
        this.name = name;
    }

}
