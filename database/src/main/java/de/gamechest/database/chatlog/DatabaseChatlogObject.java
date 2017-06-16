package de.gamechest.database.chatlog;

import lombok.Getter;

/**
 * Created by ByteList on 11.04.2017.
 */
public enum DatabaseChatlogObject {

    REPORT_ID("Report-ID"),
    TYPE("Log-Type"),
    TARGET("Target"),
    USERS("Users"),
    TIMESTAMP("Timestamp"),
    PREFIXES("Prefixes"),
    MESSAGES("Messages");

    @Getter
    private String name;

    DatabaseChatlogObject(String name) {
        this.name = name;
    }
}
