package de.gamechest.database.ban;

import java.util.ArrayList;
import java.util.List;

public enum BanUnit {
    SECOND("Sekunde(n)", 1, "s", 7),
    MINUTE("Minute(n)", 60, "min", 6),
    HOUR("Stunde(n)", 3600, "h", 5),
    DAY("Tag(e)", 86400, "d", 4),
    WEEK("Woche(n)", 604800, "w", 3),
    MONTH("Monat(e)", 2592000, "m", 2),
    YEAR("Jahr(e)", 31104000, "y", 1);

    private String name;
    private int toSecond;
    private String shortcut;
    private int rank;

    BanUnit(String name, int toSecond, String shortcut, int rank) {
        this.name = name;
        this.toSecond = toSecond;
        this.shortcut = shortcut;
        this.rank = rank;
    }

    public String getName() {
        return this.name;
    }

    public int getToSecond() {
        return this.toSecond;
    }

    public String getShortcut() {
        return this.shortcut;
    }

    public int getRank() {
        return this.rank;
    }

    public static List<String> getUnitsAsString() {
        List<String> units = new ArrayList<String>();
        for (BanUnit unit : values()) {
            units.add(unit.getShortcut().toLowerCase());
        }
        return units;
    }

    public static BanUnit getUnit(String unit) {
        for (BanUnit units : values()) {
            if (units.getShortcut().toLowerCase().equals(unit.toLowerCase())) {
                return units;
            }
        }
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShortcut(String shortcut) {
        this.shortcut = shortcut;
    }

}
