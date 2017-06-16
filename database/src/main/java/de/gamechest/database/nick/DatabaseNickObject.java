package de.gamechest.database.nick;

import lombok.Getter;

/**
 * Created by ByteList on 11.04.2017.
 */
public enum DatabaseNickObject {

    SORT_ID("Sort-Id"),
    NICK("Nickname"),
    USED("Used"),
    SKIN_TEXTURE("Skin-Texture");

    @Getter
    private String name;

    DatabaseNickObject(String name) {
        this.name = name;
    }

    public enum SkinObject {

        SIGNATURE("Signature"),
        VALUE("Value");

        @Getter
        private String name;

        SkinObject(String name) {
            this.name = name;
        }
    }
}
