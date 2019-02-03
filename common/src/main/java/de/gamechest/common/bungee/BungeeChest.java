package de.gamechest.common.bungee;

import lombok.Getter;

/**
 * Created by ByteList on 03.02.2019.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public final class BungeeChest {

    @Getter
    private static BungeeChestPlugin instance;

    public static void setInstance(BungeeChestPlugin plugin) {
        if(instance != null) {
            throw new UnsupportedOperationException("Cannot redefine singleton Chest instance");
        }
        instance = plugin;
    }

    public static final String prefix = "§2GameChest §8\u00BB ";
    public static final String pr_nick = "§5Nick §8\u00BB ";
    public static final String pr_stats = "§6Stats §8\u00BB ";
    public static final String pr_kick = "§cKick §8\u00BB ";
    public static final String pr_ban = "§cBan §8\u00BB ";
    public static final String pr_report = "§9Report §8\u00BB ";
    public static final String pr_bug = "§9BugReport §8\u00BB ";
    public static final String pr_activate = "§6Activate §8\u00BB ";
    public static final String pr_party = "§dParty §8\u00BB ";
    public static final String pr_verify = "§6Verify §8\u00BB ";

    public static final String pr_msg_private = "§f§o[§c§oPrivat§f§o] ";
    public static final String pr_msg_team = "§f§o[§c§oTeam§f§o] ";
    public static final String pr_msg_party = "§f§o[§d§oParty§f§o] ";
}
