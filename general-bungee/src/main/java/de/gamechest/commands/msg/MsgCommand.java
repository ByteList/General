package de.gamechest.commands.msg;

import de.gamechest.GameChest;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.DatabasePlayer;
import de.gamechest.database.rank.Rank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by ByteList on 05.03.2017.
 */
public class MsgCommand extends GCCommand {

    public MsgCommand() {
        super("msg", "tell");
    }

    private GameChest gameChest = GameChest.getInstance();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage("Only for a player!");
            return;
        }
        ProxiedPlayer pp = (ProxiedPlayer) sender;
        DatabasePlayer databasePlayer = gameChest.getDatabaseManager().getDatabasePlayer(pp.getUniqueId());
        if(args.length > 1) {
            String tellTo = args[0];
            ProxiedPlayer tp = gameChest.getProxy().getPlayer(tellTo);
            if(tp == null) {
                sender.sendMessage(gameChest.prefix+"§cDieser Spieler ist nicht online!");
                return;
            }

            if(tp == pp) {
                sender.sendMessage(gameChest.prefix+"§cDu kannst dir selbst keine Nachrichten schicken!");
                return;
            }

            DatabasePlayer tDatabasePlayer = gameChest.getDatabaseManager().getDatabasePlayer(tp.getUniqueId());

            if(databasePlayer.getDatabaseElement(DatabasePlayerObject.CONFIGURATIONS).getAsDocument().getInteger(DatabasePlayerObject.Configurations.MSG) == 1) {
                sender.sendMessage(gameChest.prefix+"§cDu hast die privaten Nachrichten ausgeschalten!");
                return;
            }

            if(gameChest.hasRank(pp.getUniqueId(), Rank.BUILDER))
                if(tDatabasePlayer.getDatabaseElement(DatabasePlayerObject.CONFIGURATIONS).getAsDocument().getInteger(DatabasePlayerObject.Configurations.MSG) == 1) {
                    sender.sendMessage(gameChest.prefix+"§c"+tp.getName()+" möchte keine Nachrichten erhalten!");
                    return;
                }
            String pColor = Rank.getRankById(databasePlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt()).getColor();
            String tColor = Rank.getRankById(tDatabasePlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt()).getColor();

            String message = "";

            for(int i = 1; i < args.length; i++) {
                message = message+" "+args[i];
            }

            pp.sendMessage(gameChest.pr_msg+pColor+ pp.getName()+"§7 \u00BB "+tColor+tp.getName()+"§7: §r"+message);
            tp.sendMessage(gameChest.pr_msg+pColor+ pp.getName()+"§7 \u00BB "+tColor+tp.getName()+"§7: §r"+message);

            gameChest.TELL_FROM_TO.put(pp, tp);
            gameChest.TELL_FROM_TO.put(tp, pp);
            return;
        }
        sender.sendMessage(gameChest.prefix+"§c/msg <Spieler> <Nachricht>");
    }
}
