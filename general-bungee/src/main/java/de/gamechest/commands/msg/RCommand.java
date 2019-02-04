package de.gamechest.commands.msg;

import de.gamechest.GameChest;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.DatabasePlayer;
import de.gamechest.common.Rank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by ByteList on 05.03.2017.
 */
public class RCommand extends GCCommand {

    public RCommand() {
        super("r");
    }

    private GameChest gameChest = GameChest.getInstance();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage("Only for a player!");
            return;
        }
        ProxiedPlayer pp = (ProxiedPlayer) sender;
        DatabasePlayer databasePlayer = new DatabasePlayer(gameChest.getDatabaseManager(), pp.getUniqueId());
        if(args.length > 0) {
            ProxiedPlayer tp = gameChest.TELL_FROM_TO.get(pp);
            if(tp == null) {
                sender.sendMessage(gameChest.prefix+"§cDieser Spieler ist nicht online!");
                return;
            }

            if(tp == pp) {
                sender.sendMessage(gameChest.prefix+"§cDu kannst dir selbst keine Nachrichten schicken!");
                return;
            }

            DatabasePlayer tDatabasePlayer = new DatabasePlayer(gameChest.getDatabaseManager(), tp.getUniqueId());

            if(databasePlayer.getDatabaseElement(DatabasePlayerObject.CONFIGURATIONS).getAsDocument().getInteger(DatabasePlayerObject.Configurations.MSG.getName()) == 1) {
                sender.sendMessage(gameChest.prefix+"§cDu hast privaten Nachrichten ausgeschalten!");
                return;
            }

            if(gameChest.hasRank(pp.getUniqueId(), Rank.BUILDER))
                if(tDatabasePlayer.getDatabaseElement(DatabasePlayerObject.CONFIGURATIONS).getAsDocument().getInteger(DatabasePlayerObject.Configurations.MSG.getName()) == 1) {
                    sender.sendMessage(gameChest.prefix+"§c"+tp.getName()+" möchte keine Nachrichten erhalten!");
                    return;
                }
            String pColor = Rank.getRankById(databasePlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt()).getColor();
            String tColor = Rank.getRankById(tDatabasePlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt()).getColor();

            String message = "";

            for (String arg : args) {
                message = message + " " + arg;
            }

            pp.sendMessage(gameChest.pr_msg_private +pColor+ pp.getName()+"§7 \u00BB "+tColor+tp.getName()+"§7:§r"+message);
            tp.sendMessage(gameChest.pr_msg_private +pColor+ pp.getName()+"§7 \u00BB "+tColor+tp.getName()+"§7:§r"+message);

            gameChest.TELL_FROM_TO.put(pp, tp);
            gameChest.TELL_FROM_TO.put(tp, pp);
            return;
        }
        sender.sendMessage(gameChest.prefix+"§c/r <Nachricht>");
    }
}
