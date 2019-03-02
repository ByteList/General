package de.gamechest.commands;

import de.bytelist.bytecloud.core.ByteCloudCore;
import de.gamechest.GameChest;
import de.gamechest.common.ChestPrefix;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by ByteList on 26.03.2017.
 */
public class ServerIdCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        commandSender.sendMessage(ChestPrefix.PREFIX+"§6Deine aktuelle ServerID: §a"+
                (GameChest.getInstance().isCloudEnabled() ? ByteCloudCore.getInstance().getCloudHandler().getServerId() : Bukkit.getServerName()));
        return true;
    }
}
