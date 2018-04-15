package de.gamechest.database.webregister;

import lombok.Getter;

/**
 * Created by ByteList on 11.04.2017.
 */
public enum DatabaseWebRegisterObject {

    UUID("UUID"),
    MAIL_ADDRESS("Mail-Address"),
    VERIFY_CODE("Verify-Code");

    @Getter
    private String name;

    DatabaseWebRegisterObject(String name) {
        this.name = name;
    }

}
