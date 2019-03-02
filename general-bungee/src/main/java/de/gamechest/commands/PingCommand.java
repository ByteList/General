package de.gamechest.commands;

import de.gamechest.GameChest;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.common.ChestPrefix;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by ByteList on 23.11.2016.
 */
public class PingCommand extends GCCommand {

    public PingCommand() {
        super("ping");
    }

    private GameChest gameChest = GameChest.getInstance();

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(!(sender instanceof ProxiedPlayer))
            sender.sendMessage(ChestPrefix.PREFIX+"§7Dein aktueller Ping: §21");
        else {
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            String color;
            int ping = pp.getPing();
            if(ping <= 20) color = "§2";
            else if(ping <= 35) color = "§a";
            else if(ping <= 45) color = "§e";
            else if(ping <= 50) color = "§6";
            else if(ping <= 70) color = "§c";
            else color = "§4";


            pp.sendMessage(ChestPrefix.PREFIX+"§7Dein aktueller Ping: "+color+ping);
        }
    }
}
