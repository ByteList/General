package de.gamechest.database.stats.network;

import lombok.Getter;

/**
 * Created by ByteList on 11.04.2017.
 */
public enum DatabaseNetworkStatsObject {

    UUID("UUID"),
    MINECRAFT("Minecraft"),
    NETWORK("Network");


    @Getter
    private String name;

    DatabaseNetworkStatsObject(String name) {
        this.name = name;
    }

}
