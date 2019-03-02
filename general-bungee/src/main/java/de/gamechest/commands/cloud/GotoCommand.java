package de.gamechest.commands.cloud;

import de.bytelist.bytecloud.bungee.ByteCloudMaster;
import de.bytelist.bytecloud.common.Cloud;
import de.gamechest.GameChest;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.common.Rank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by ByteList on 21.11.2016.
 */
public class GotoCommand extends GCCommand {

    public GotoCommand() {
        super("goto");
    }

    private GameChest gameChest = GameChest.getInstance();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer))
            sender.sendMessage(Cloud.PREFIX + "§cDu bist die Konsole und kannst diesen Befehl nicht ausführen!");
        else {
            if (!gameChest.isCloudEnabled()) return;
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            if (!gameChest.hasRank(pp.getUniqueId(), Rank.BUILDER)) {
                gameChest.sendNoPermissionMessage(sender);
                return;
            }

            if (args.length == 1) {
                String playername = args[0];
                ProxiedPlayer target = gameChest.getProxy().getPlayer(playername);
                int con = ByteCloudMaster.getInstance().getCloudHandler().move(pp, target);

                if (con == 0) {
                    pp.sendMessage(Cloud.PREFIX + "§7Verbinde zum Server " + target.getServer().getInfo().getName() + "...");
                    return;
                }
                if (con == 1) {
                    pp.sendMessage(Cloud.PREFIX + "§cDu befindest dich schon auf diesem Server!");
                    return;
                }
                if (con == 2) {
                    pp.sendMessage(Cloud.PREFIX + "§c" + playername + " ist nicht online!");
                    return;
                }
                pp.sendMessage(Cloud.PREFIX + "§cError: Konnte keine richtige ID finden: " + con);
                return;
            }
            pp.sendMessage(Cloud.PREFIX + "§c/goto <Spieler>");
        }
    }
}
