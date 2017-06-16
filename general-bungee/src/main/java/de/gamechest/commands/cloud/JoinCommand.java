package de.gamechest.commands.cloud;

import de.bytelist.bytecloud.bungee.ByteCloudMaster;
import de.gamechest.GameChest;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.rank.Rank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by ByteList on 21.11.2016.
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
            if(gameChest.getProxy().getPluginManager().getPlugin("ByteCloud-Master") == null) return;
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            if(!gameChest.hasRank(pp.getUniqueId(), Rank.BUILDER)) {
                pp.sendMessage(ByteCloudMaster.getInstance().prefix+"§cDu hast keine Berechtigung für diesen Befehl!");
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
                pp.sendMessage(ByteCloudMaster.getInstance().prefix+"§cError: Konnte keine richtige ID finden: "+con);
                return;
            }
            if(args.length == 2) {
                String srvid = args[0];
                ProxiedPlayer target = GameChest.getInstance().getProxy().getPlayer(args[1]);

                if(target == null) {
                    pp.sendMessage(ByteCloudMaster.getInstance().prefix+"§cDer Spieler ist nicht online!");
                    return;
                }
                int rankId = GameChest.getInstance().getDatabaseManager().getDatabasePlayer(pp.getUniqueId()).getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt();
                if(rankId > Rank.DEVELOPER.getId() &&
                        GameChest.getInstance().getDatabaseManager().getDatabasePlayer(target.getUniqueId()).getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt()
                        < rankId) {
                    pp.sendMessage(ByteCloudMaster.getInstance().prefix+"§cDu hast keine Berechtigung diesen Spieler zu verschieben!");
                    return;
                }

                if(srvid.contains("-")) {
                    String[] srv = srvid.split("-");

                    if(srv.length == 2)
                        srvid = ByteCloudMaster.getInstance().getCloudHandler().getUniqueServerId(srvid);
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
                pp.sendMessage(ByteCloudMaster.getInstance().prefix+"§cError: Konnte keine richtige ID finden: "+con);
                return;
            }
            pp.sendMessage(ByteCloudMaster.getInstance().prefix+"§c/join <Server> (Spieler)");
        }
    }
}