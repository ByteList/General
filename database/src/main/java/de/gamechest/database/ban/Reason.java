package de.gamechest.database.ban;


import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public enum Reason {

    CLIENT("Verbotenene Modifikation", 3, BanUnit.MONTH),
    TEAMING("Unerlaubtes Teaming", 15, BanUnit.DAY),
    WERBUNG("Werbung", 1, BanUnit.WEEK),
    SKIN("Verbotener Skin", 15, BanUnit.DAY),
    NAME("Verbotener Namensinhalt", 15, BanUnit.DAY),
    WORTWAHL("Falsche Wortwahl", 2, BanUnit.DAY),
    TROLLING("Trolling anderer Spieler", 15, BanUnit.DAY),
    BAN_UMGEHUNG("Umgehung einer Sperre", -1, BanUnit.SECOND);


    @Getter
    private String reason;
    @Getter
    private Integer time;
    @Getter
    private BanUnit value;
    @Getter
    private Integer points;

    Reason(String reason, Integer time, BanUnit value) {
        this.reason = reason;
        this.time = time;
        this.value = value;
        this.points = time;
    }

    public static List<String> getReasonsAsString() {
        List<String> units = new ArrayList<>();
        for (Reason unit : values()) {
            units.add(unit.toString().toUpperCase());
        }
        return units;
    }

    public static Reason getReason(String unit) {
        for (Reason units : values()) {
            if (units.toString().toUpperCase().equals(unit.toUpperCase())) {
                return units;
            }
        }
        throw new IllegalArgumentException(unit + " is not a ban reason!");
    }
}
