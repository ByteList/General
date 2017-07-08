package de.gamechest.commands.rank;

import de.gamechest.GameChest;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayerObject;
import de.gamechest.database.rank.Rank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by ByteList on 28.02.2017.
 */
public class ToggleRankCommand extends GCCommand {

    public ToggleRankCommand() {
        super("togglerank", "tr");
    }

    private GameChest gameChest = GameChest.getInstance();

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage("§cNur für Spieler!");
            return;
        }
        ProxiedPlayer pp = (ProxiedPlayer) sender;

        if(!gameChest.hasRank(pp.getUniqueId(), Rank.PREMIUM)) {
            pp.sendMessage(gameChest.prefix+"§cDu hast keine Berechtigung für diesen Befehl!");
            return;
        }

        if(!pp.getServer().getInfo().getName().contains("lb")) {
            pp.sendMessage(gameChest.prefix+"§cDu kannst deinen Rang nur in der Lobby umschalten!");
            return;
        }

        gameChest.getDatabaseManager().getAsync().getOnlinePlayer(pp.getUniqueId(), dbOPlayer-> {
            if(!dbOPlayer.getDatabaseElement(DatabaseOnlinePlayerObject.TOGGLED_RANK).getAsBoolean()) {
                pp.sendMessage(gameChest.prefix+"§aDein Rang ist nun in den Minigames unsichtbar.");
                dbOPlayer.setDatabaseObject(DatabaseOnlinePlayerObject.TOGGLED_RANK, true);
            } else {
                pp.sendMessage(gameChest.prefix + "§eDein Rang ist nun in den Minigames sichtbar.");
                dbOPlayer.setDatabaseObject(DatabaseOnlinePlayerObject.TOGGLED_RANK, false);
            }
        });
    }
}
