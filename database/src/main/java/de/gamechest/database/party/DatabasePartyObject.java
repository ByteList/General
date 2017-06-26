package de.gamechest.database.party;

import lombok.Getter;

/**
 * Created by ByteList on 15.04.2017.
 */
public enum DatabasePartyObject {

    PARTY_ID("Party-Id"),
    LEADER("Leader"),
    MEMBERS("Members");

    @Getter
    private String name;

    DatabasePartyObject(String name) {
        this.name = name;
    }

}
