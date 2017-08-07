package de.gamechest.commands;

import de.gamechest.GameChest;
import de.gamechest.commands.base.GCCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;

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
        sender.sendMessage(gameChest.prefix+"§7~ Coming soon ~");
        return;
//        if(args.length == 1) {
//            final String[] name = {args[0]};
//            UUID uuid = UUIDFetcher.getUUID(name[0]);
//
//            if(uuid == null) {
//                sender.sendMessage(gameChest.prefix+"§cKonnte den User nicht in der Datenbank finden!");
//                return;
//            }
//
//            gameChest.getDatabaseManager().getAsync().getPlayer(uuid, dbPlayer -> {
//                name[0] = dbPlayer.getDatabaseElement(DatabasePlayerObject.LAST_NAME).getAsString();
//
//                Long seconds = dbPlayer.getDatabaseElement(DatabasePlayerObject.ONLINE_TIME).getAsLong();
//
//                double minutes = seconds/60;
//                double hours = minutes/60;
//
//                DecimalFormat format = new DecimalFormat("0.00");
//
//                sender.sendMessage(gameChest.prefix+"§6"+ name[0] +"§a war bisher §6"+
//                        format.format(hours).replace(",", ".")+"§a Stunden online.");
//            });
//
//            return;
//        }
//
//        if(!(sender instanceof ProxiedPlayer)) {
//            sender.sendMessage("/onlinetime <Spieler>");
//            return;
//        }
//        ProxiedPlayer pp = (ProxiedPlayer) sender;
//        gameChest.getDatabaseManager().getAsync().getPlayer(pp.getUniqueId(), dbPlayer -> {
//            Long seconds = dbPlayer.getDatabaseElement(DatabasePlayerObject.ONLINE_TIME).getAsLong();
//
//            double minutes = seconds/60;
//            double hours = minutes/60;
//
//            DecimalFormat format = new DecimalFormat("0.00");
//
//            sender.sendMessage(gameChest.prefix+"§aDu warst bisher §6"+
//                    format.format(hours).replace(",", ".")+"§a Stunden online.");
//        });
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {
//        if (strings.length == 0) {
//            ArrayList<String> tabs = new ArrayList<>();
//            for (ProxiedPlayer pp : gameChest.getProxy().getPlayers()) {
//                DatabaseOnlinePlayer databaseOnlinePlayer = new DatabaseOnlinePlayer(gameChest.getDatabaseManager(), pp.getUniqueId().toString(), pp.getName());
//                if (databaseOnlinePlayer.getDatabaseElement(DatabaseOnlinePlayerObject.NICKNAME) != null)
//                    tabs.add(databaseOnlinePlayer.getDatabaseElement(DatabaseOnlinePlayerObject.NICKNAME).getAsString());
//                else tabs.add(pp.getName());
//            }
//            return tabs;
//        }
//
        return new ArrayList<>();
    }
}
