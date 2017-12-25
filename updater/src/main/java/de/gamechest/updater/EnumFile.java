package de.gamechest.updater;


import lombok.Getter;

/**
 * Created by ByteList on 31.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public enum EnumFile {

    LOGS("./logs/"),
    DOWNLOADS("./downloads");

    @Getter
    private String path;

    EnumFile(String path) {
        this.path = path;
    }
}
