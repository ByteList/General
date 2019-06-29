package de.gamechest;

import de.gamechest.common.Chest;
import de.gamechest.common.Rank;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by ByteList on 29.06.2019.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PermissionCheck implements de.bytelist.bytecloud.common.CloudPermissionCheck<ProxiedPlayer> {

    @Override
    public boolean hasPermission(String permission, ProxiedPlayer checker) {
        return GameChest.getInstance().hasRank(checker.getUniqueId(), Chest.getPermissionRank().getOrDefault(permission, Rank.DEVELOPER));
    }

    @Override
    public String getNoPermissionMessage() {
        return "§cDu hast keine Berechtigung für diesen Befehl!";
    }
}
