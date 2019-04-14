package de.gamechest.commands;

import de.bytelist.bytecloud.bungee.ByteCloudMaster;
import de.bytelist.bytecloud.common.CloudPlayer;
import de.bytelist.bytecloud.common.bungee.BungeeCloud;
import de.bytelist.bytecloud.common.server.CloudServer;
import de.gamechest.GameChest;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.common.ChestPrefix;
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

//        if(!ByteCloudMaster.getInstance().getForcedJoinServerId().equals("-1")) {
//            serverId = display = ByteCloudMaster.getInstance().getForcedJoinServerId();
//        } else {
            serverId = ByteCloudMaster.getInstance().getCloudHandler().getRandomLobbyId();
            display = "Lobby";
//        }

        CloudPlayer cloudPlayer = BungeeCloud.getInstance().getCloudAPI().getPlayer(pp.getUniqueId());
        CloudServer targetServer = BungeeCloud.getInstance().getCloudAPI().getServer(serverId);

        if(targetServer== null) {
            pp.sendMessage(ChestPrefix.PREFIX+"§cKonnte keinen "+display+"-Server finden! Bitte melde dies dem Support!");
            return;
        }

        if(cloudPlayer.getCurrentServer() == targetServer) {
            pp.sendMessage(ChestPrefix.PREFIX+"§6Du befindest dich bereits auf dem "+display+"-Server!");
            return;
        }

        pp.sendMessage(ChestPrefix.PREFIX+"§eVerbinde zum "+display+"-Server...");
        BungeeCloud.getInstance().getCloudAPI().movePlayerToServer(pp.getUniqueId(), serverId);
    }
}
