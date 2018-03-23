package de.gamechest.commands;

import de.bytelist.bytecloud.bungee.ByteCloudMaster;
import de.gamechest.GameChest;
import de.gamechest.commands.base.GCCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by ByteList on 20.02.2017.
 */
public class HubCommand extends GCCommand {

    public HubCommand() {
        super("hub", "lobby", "l", "lb");
    }
    
    private GameChest gameChest = GameChest.getInstance();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage("§cDu bist kein Spieler!");
            return;
        }

        ProxiedPlayer pp = (ProxiedPlayer) sender;

        String serverId, display;

        if(!ByteCloudMaster.getInstance().getServerIdOnConnect().equals("-1")) {
            serverId = display = ByteCloudMaster.getInstance().getServerIdOnConnect();
        } else {
            serverId = ByteCloudMaster.getInstance().getCloudHandler().getRandomLobbyId();
            display = "Lobby";
        }

        int i = ByteCloudMaster.getInstance().getCloudHandler().connect(serverId, pp);

        if(i == 0) {
            pp.sendMessage(gameChest.prefix+"§eVerbinde zum "+display+"-Server...");
            return;
        }
        if(i == 1) {
            pp.sendMessage(gameChest.prefix+"§6Du befindest dich bereits auf dem "+display+"-Server!");
            return;
        }
        if(i == 2) {
            pp.sendMessage(gameChest.prefix+"§cKonnte keinen "+display+"-Server finden! Bitte melde dies dem Support!");
            return;
        }
        pp.sendMessage(gameChest.prefix+"§cError: Konnte keine passende Aktion ausführen!");
    }
}
