package de.gamechest.commands.cloud;

import de.bytelist.bytecloud.bungee.ByteCloudMaster;
import de.bytelist.bytecloud.network.bungee.packet.PacketInStopServer;
import de.gamechest.GameChest;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.database.rank.Rank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by ByteList on 06.02.2017.
 */
public class StopCommand extends GCCommand {


    public StopCommand() {
        super("stop", "shutdown");
    }

    private GameChest gameChest = GameChest.getInstance();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!gameChest.isCloudEnabled()) return;
        ByteCloudMaster byteCloudMaster = ByteCloudMaster.getInstance();
        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer pp = (ProxiedPlayer) sender;

            if(!gameChest.hasRank(pp.getUniqueId(), Rank.DEVELOPER)) {
                sender.sendMessage(ByteCloudMaster.getInstance().prefix+"§cDu hast keine Berechtigung für diesen Befehl!");
                return;
            }

            ServerInfo currentServer = pp.getServer().getInfo();

            new Thread("Cloud-Stop-"+currentServer.getName()) {
                @Override
                public void run() {
//                    while (true) {
//                        if(currentServer.getPlayers().size() == 0) break;
//                    }
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    PacketInStopServer packetInStopServer = new PacketInStopServer(currentServer.getName(), sender.getName());
                    byteCloudMaster.getBungeeClient().sendPacket(packetInStopServer);
                }
            }.start();
        }
    }
}
