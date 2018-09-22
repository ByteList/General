package de.gamechest.commands.cloud;

import de.bytelist.bytecloud.ServerIdResolver;
import de.bytelist.bytecloud.bungee.ByteCloudMaster;
import de.gamechest.GameChest;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.database.DatabasePlayer;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.rank.Rank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by ByteList on 21.11.2016.
 *
 * Copyright by ByteList - https://bytelist.de/
 */
public class JoinCommand extends GCCommand {

    public JoinCommand() {
        super("join", "connect");
    }

    private GameChest gameChest = GameChest.getInstance();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer))
            sender.sendMessage(ByteCloudMaster.getInstance().prefix+"§cDu bist die Konsole und kannst diesen Befehl nicht ausführen!");
        else {
            if(!gameChest.isCloudEnabled()) return;
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            if(!gameChest.hasRank(pp.getUniqueId(), Rank.BUILDER)) {
                gameChest.sendNoPermissionMessage(sender);
                return;
            }

            if(args.length == 1) {
                //lb-01-asdfijh
                String srvid = args[0];

                if(srvid.contains("-")) {
                    String[] srv = srvid.split("-");

                    if(srv.length == 2)
                        srvid = ByteCloudMaster.getInstance().getCloudHandler().getUniqueServerId(srvid);
                }

                int con = ByteCloudMaster.getInstance().getCloudHandler().connect(srvid, pp);

                if(con == 0) {
                    pp.sendMessage(ByteCloudMaster.getInstance().prefix+"§7Verbinde zum Server "+srvid+"...");
                    return;
                }
                if(con == 1) {
                    pp.sendMessage(ByteCloudMaster.getInstance().prefix+"§cDu befindest dich schon auf diesem Server!");
                    return;
                }
                if(con == 2) {
                    pp.sendMessage(ByteCloudMaster.getInstance().prefix+"§cDer Server existiert nicht!");
                    return;
                }
                pp.sendMessage(ByteCloudMaster.getInstance().prefix+"§cError: ("+con+") Konnte keine richtige ID finden.");
                return;
            }
            if(args.length == 2) {
                String srvid = ServerIdResolver.getUniqueServerId(args[0]);

                if(args[0].equals("@all")) {
                    if(!gameChest.hasRank(pp.getUniqueId(), Rank.ADMIN)) {
                        gameChest.sendNoPermissionMessage(pp);
                        return;
                    }

                    ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(srvid);
                    ProxyServer.getInstance().getPlayers().forEach(proxiedPlayer -> proxiedPlayer.connect(serverInfo));

                    ProxyServer.getInstance().broadcast(ByteCloudMaster.getInstance().prefix+"§7Alle Spieler wurden auf §e"+srvid+"§7 verschoben!");
                    return;
                }

                ProxiedPlayer target = GameChest.getInstance().getProxy().getPlayer(args[1]);

                if(target == null) {
                    pp.sendMessage(ByteCloudMaster.getInstance().prefix+"§cDer Spieler ist nicht online!");
                    return;
                }
                int rankId = new DatabasePlayer(gameChest.getDatabaseManager(), pp.getUniqueId()).getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt();
                if(rankId > Rank.DEVELOPER.getId() &&
                        new DatabasePlayer(gameChest.getDatabaseManager(), target.getUniqueId()).getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt()
                        < rankId) {
                    pp.sendMessage(ByteCloudMaster.getInstance().prefix+"§cDu hast keine Berechtigung diesen Spieler zu verschieben!");
                    return;
                }

                int con = ByteCloudMaster.getInstance().getCloudHandler().connect(srvid, target);

                if(con == 0) {
                    target.sendMessage(ByteCloudMaster.getInstance().prefix+"§7Verbinde zum Server "+srvid+"...");
                    pp.sendMessage(ByteCloudMaster.getInstance().prefix+"§7Verschiebe "+target.getName()+" zum Server "+srvid+"...");
                    return;
                }
                if(con == 1) {
                    pp.sendMessage(ByteCloudMaster.getInstance().prefix+"§c"+target.getName()+" befindest dich schon auf diesem Server!");
                    return;
                }
                if(con == 2) {
                    pp.sendMessage(ByteCloudMaster.getInstance().prefix+"§cDer Server existiert nicht!");
                    return;
                }
                pp.sendMessage(ByteCloudMaster.getInstance().prefix+"§cError: ("+con+") Konnte keine richtige ID finden.");
                return;
            }
            pp.sendMessage(ByteCloudMaster.getInstance().prefix+"§c/join <Server> (Spieler)");
        }
    }
}
