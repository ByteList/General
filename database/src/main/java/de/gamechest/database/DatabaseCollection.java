package de.gamechest.database;

import lombok.Getter;

/**
 * Created by ByteList on 09.04.2017.
 */
public enum DatabaseCollection {

    PLAYERS("players"),
    ONLINE_PLAYER("online-player"),
    PREMIUM_PLAYER("premium-player"),
    UUID_BUFFER("uuid-buffer"),
    ACTIVATE_CODES("activate-codes"),

    PARTY("party"),

    POLL("poll"),

    BANS("bans"),
    BAN_HISTORY("ban-history"),
    CHAT_REPORTS("chat-reports"),

    NICKNAMES("nicknames"),

    CA_STATISTICS("ca-statistics"),
    SD_STATISTICS("sd-statistics"),
    DR_STATISTICS("dr-statistics"),
    JD_STATISTICS("jd-statistics"),

    BUG_REPORTS("bug-reports");

    @Getter
    private String name;

    DatabaseCollection(String name) {
        this.name = name;
    }
}
