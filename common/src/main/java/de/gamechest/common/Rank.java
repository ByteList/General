package de.gamechest.common;

import lombok.Getter;

import java.util.ArrayList;

/**
 * Created by ByteList on 09.04.2017.
 */
public enum Rank {

    SPIELER(8, "Spieler", "§9", "§9Spieler §8\u00BB §9", "Spieler"),
    PREMIUM(7, "Premium", "§6", "§6Premium §8\u00BB §6", "Premium"),
    VIP(6, "VIP", "§5", "§5VIP §8\u00BB §5", "VIP"),
    BUILDER(5, "Builder", "§e", "§eBuilder §8\u00BB §e", "Builder"),
    SUPPORTER(4, "Supporter", "§a", "§aSup §8\u00BB §a", "Sup"),
    MODERATOR(3, "Moderator", "§2", "§2Mod §8\u00BB §2", "Mod"),
    DEVELOPER(2, "Developer", "§b", "§bDev §8\u00BB §b", "Dev"),
    ADMIN(1, "Admin", "§4", "§4Admin §8\u00BB §4", "Admin"),
    NULL(0, "{null}", "§f", "§fNULL - ", "{NULL}");

    @Getter
    private Integer id;
    @Getter
    private String name;
    @Getter
    private String color;
    @Getter
    private String prefix;
    @Getter
    private String shortName;

    Rank(Integer rankNr, String name, String rankColor, String rankPrefix, String shortName) {
        this.id = rankNr;
        this.name = name;
        this.color = rankColor;
        this.prefix = rankPrefix;
        this.shortName = shortName;
    }

    public static boolean existsRank(String group) {
        ArrayList<String> list = new ArrayList<>();
        for(Rank group1 : Rank.values()) list.add(group1.toString());

        return list.contains(group.toUpperCase());
    }

    public static Rank getRankById(Integer id) {
        for(Rank ranks : Rank.values()) {
            int rank_id = ranks.id;

            if(id == rank_id)
                return ranks;
        }
        return Rank.NULL;
    }
}
