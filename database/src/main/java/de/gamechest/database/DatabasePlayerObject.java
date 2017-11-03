package de.gamechest.database;

import lombok.Getter;

/**
 * Created by ByteList on 10.04.2017.
 */
public enum DatabasePlayerObject {

    UUID("UUID"),
    RANK_ID("Rank-Id"),
    OPERATOR("Operator"),
    COINS("Coins"),
    LAST_IP("Last-IP"),
    LAST_NAME("Last-Name"),
    BAN_POINTS("Ban-Points"),
    ONLINE_TIME("Online-Time"),
    FIRST_LOGIN("First-Login"),
    LAST_LOGIN("Last-Login"),
    TS_UID("Ts-Uid"),
    LAST_DAILY_REWARD("Last-Daily-Reward"),
    FOUND_SECRETS("Found-Secrets"),
    BOUGHT_SHOP_ITEMS("Bought-Shop-Items"),
    LOBBY_INVENTORY("Lobby-Inventory"),
    ACTIVE_SHOP_ITEMS("ActiveShopItems"),
    CONFIGURATIONS("Configurations"),
    SKIN_TEXTURE("Skin-Texture");

    @Getter
    private String name;

    DatabasePlayerObject(String name) {
        this.name = name;
    }

    public static String[] toStringList(DatabasePlayerObject... rules) {
        String[] list = new String[rules.length];
        for (int i = 0; i < rules.length; i++)
            list[i] = rules[i].getName();
        return list;
    }

    public enum ActiveShopItems {

        HEAD("HEAD"),
        ARMOR("ARMOR"),
        DUST("DUST"),
        GADGET("GADGET");

        @Getter
        private String name;

        ActiveShopItems(String name) {
            this.name = name;
        }
    }

    public enum Configurations {

        LOBBY_POS_X("lobby:posX"),
        LOBBY_POS_Y("lobby:posY"),
        LOBBY_POS_Z("lobby:posZ"),
        LOBBY_PITCH("lobby:pitch"),
        LOBBY_YAW("lobby:yaw"),
        LOBBY_CHAT("lobby:chat"),
        LOBBY_VISIBILITY("lobby:visibility"),
        LOBBY_PARTY_TAG("lobby:party_tag"),
        LOBBY_PARTICLES_VISIBILITY("lobby:particles_visibility"),
        MSG("network:msg"),
        BUILD_CONNECT("build:connect"),
        BUILD_MODE("build:mode");

        @Getter
        private String name;

        Configurations(String name) {
            this.name = name;
        }
    }
}
