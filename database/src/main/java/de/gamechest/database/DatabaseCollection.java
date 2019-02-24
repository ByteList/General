package de.gamechest.database;

import lombok.Getter;

/**
 * Created by ByteList on 09.04.2017.
 */
public enum DatabaseCollection {    // Last: NETWORK_STATISTICS

    PLAYERS(0, "players"),
    ONLINE_PLAYER(1, "online-player"),
    PREMIUM_PLAYER(2, "premium-player"),
    UUID_BUFFER(3, "uuid-buffer"),
    ACTIVATE_CODES(4, "activate-codes"),
    PARTY(5, "party"),

    POLL(6, "poll"),

    NICKNAMES(7, "nicknames"),

    CA_STATISTICS(8, "ca-statistics"),
    SD_STATISTICS(9, "sd-statistics"),
    DR_STATISTICS(10, "dr-statistics"),
    JD_STATISTICS(11, "jd-statistics"),
    NETWORK_STATISTICS(18, "network-statistics"),

    BANS(12, "bans"),
    BAN_HISTORY(13, "ban-history"),
    CHAT_REPORTS(14, "chat-reports"),
    BUG_REPORTS(15, "bug-reports"),

    WEB_REGISTER(16, "web-register"),
    TERMS(17, "terms");

    @Getter
    private int id;
    @Getter
    private String name;

    DatabaseCollection(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static DatabaseCollection getDatabaseCollectionFromId(int id) {
        for(DatabaseCollection collection : values()) {
            if(id == collection.getId()) {
                return collection;
            }
        }
        throw new IllegalArgumentException("This collection doesn't exist! ("+id+")");
    }
}
