package de.gamechest;

import de.bytelist.bytecloud.common.CloudPermissionCheck;
import de.gamechest.common.Chest;
import de.gamechest.common.Rank;
import org.bukkit.entity.Player;

/**
 * Created by ByteList on 29.06.2019.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PermissionCheck implements CloudPermissionCheck<Player> {

    @Override
    public boolean hasPermission(String permission, Player checker) {
        return GameChest.getInstance().hasRank(checker.getUniqueId(), Chest.getPermissionRank().getOrDefault(permission, Rank.DEVELOPER));
    }

    @Override
    public String getNoPermissionMessage() {
        return "§cDu hast keine Berechtigung für diesen Befehl!";
    }
}
