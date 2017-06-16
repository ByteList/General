package de.gamechest.commands;

import de.bytelist.bytecloud.core.ByteCloudCore;
import de.gamechest.GameChest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by ByteList on 26.03.2017.
 */
public class ServerIdCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        commandSender.sendMessage(GameChest.getInstance().prefix+"§6Deine aktuelle ServerID: §a"+ ByteCloudCore.getInstance().getCloudHandler().getServerId());
        return true;
    }
}
