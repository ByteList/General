package de.gamechest.common.spigot;

import de.gamechest.common.ChestPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by ByteList on 03.02.2019.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public interface SpigotChestPlugin extends ChestPlugin {

    public String getDisplayname(Player player);

    public void sendNoPermissionMessage(CommandSender sender);
}
