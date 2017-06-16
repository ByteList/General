package de.gamechest.commands;

import de.gamechest.GameChest;
import de.gamechest.UUIDFetcher;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.DatabasePlayer;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayer;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayerObject;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by ByteList on 28.02.2017.
 */
public class OnlineTimeCommand extends GCCommand implements TabExecutor {

    public OnlineTimeCommand() {
        super("onlinetime", "ontime", "onlinet");
    }

    private GameChest gameChest = GameChest.getInstance();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 1) {
            String name = args[0];
            UUID uuid = UUIDFetcher.getUUID(name);
            DatabaseOnlinePlayer databaseOnlinePlayer = gameChest.getDatabaseManager().getDatabaseOnlinePlayer(uuid);
            DatabasePlayer databasePlayer = gameChest.getDatabaseManager().getDatabasePlayer(uuid);
            if(databaseOnlinePlayer.getDatabaseElement(DatabaseOnlinePlayerObject.NICKNAME) != null) {
                name = databaseOnlinePlayer.getDatabaseElement(DatabaseOnlinePlayerObject.NAME).getAsString();
            } else
                name = databasePlayer.getDatabaseElement(DatabasePlayerObject.LAST_NAME).getAsString();

            if(!databasePlayer.existsPlayer()) {
                sender.sendMessage(gameChest.prefix+"§cKonnte den User nicht in der Datenbank finden!");
                return;
            }

            Long seconds = databasePlayer.getDatabaseElement(DatabasePlayerObject.ONLINE_TIME).getAsLong();

            double minutes = seconds/60;
            double hours = minutes/60;

            DecimalFormat format = new DecimalFormat("0.00");


            sender.sendMessage(gameChest.prefix+"§6"+name+"§a war bisher §6"+
                    format.format(hours).replace(",", ".")+"§a Stunden online.");
            return;
        }

        if(!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage("/onlinetime <Spieler>");
            return;
        }
        ProxiedPlayer pp = (ProxiedPlayer) sender;
        DatabasePlayer databasePlayer = gameChest.getDatabaseManager().getDatabasePlayer(pp.getUniqueId());

        Long seconds = databasePlayer.getDatabaseElement(DatabasePlayerObject.ONLINE_TIME).getAsLong();

        double minutes = seconds/60;
        double hours = minutes/60;

        DecimalFormat format = new DecimalFormat("0.00");
        sender.sendMessage(gameChest.prefix+"§aDu warst bisher §6"+
                format.format(hours).replace(",", ".")+"§a Stunden online.");
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {
        if (strings.length == 0) {
            ArrayList<String> tabs = new ArrayList<>();
            for (ProxiedPlayer pp : gameChest.getProxy().getPlayers()) {
                DatabaseOnlinePlayer databaseOnlinePlayer = gameChest.getDatabaseManager().getDatabaseOnlinePlayer(pp.getUniqueId());
                if (databaseOnlinePlayer.getDatabaseElement(DatabaseOnlinePlayerObject.NICKNAME) != null)
                    tabs.add(databaseOnlinePlayer.getDatabaseElement(DatabaseOnlinePlayerObject.NICKNAME).getAsString());
                else tabs.add(pp.getName());
            }
            return tabs;
        }

        return new ArrayList<>();
    }
}
