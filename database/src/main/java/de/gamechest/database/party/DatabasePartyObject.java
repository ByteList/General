package de.gamechest.database.party;

import lombok.Getter;

public enum DatabasePartyObject {

    PARTY_ID("Party-Id"),
    LEADER("Leader"),
    MEMBERS("Members");

    /**
     * Created by ByteList on 15.04.2017.
     *
     * Copyright by ByteList - https://bytelist.de/
     */
    @Getter
    private String name;

    DatabasePartyObject(String name) {
        this.name = name;
    }
}
