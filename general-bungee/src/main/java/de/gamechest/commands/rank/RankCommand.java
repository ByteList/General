package de.gamechest.commands.rank;

import de.gamechest.GameChest;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.common.ChestPrefix;
import de.gamechest.common.Rank;
import de.gamechest.common.UUIDFetcher;
import de.gamechest.database.DatabasePlayer;
import de.gamechest.database.DatabasePlayerObject;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/**
 * Created by ByteList on 19.11.2016.
 */
public class RankCommand extends GCCommand {

    private GameChest gameChest = GameChest.getInstance();

    public RankCommand() {
        super("rank");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            if (!gameChest.hasRank(pp.getUniqueId(), Rank.DEVELOPER)) {
                gameChest.sendNoPermissionMessage(sender);
                return;
            }
        }

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("list")) {
                sender.sendMessage(ChestPrefix.PREFIX+"§7Rang-Liste:");
                for(Rank rank : Rank.values()) {
                    sender.sendMessage("§8\u00BB " + rank.getPrefix() + sender.getName());
                }
                return;
            }
        }
        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("info")) {
                String name = args[1];
                UUID uuid = UUIDFetcher.getUUID(name);
                DatabasePlayer databasePlayer = new DatabasePlayer(gameChest.getDatabaseManager(), uuid);

                if(!databasePlayer.existsPlayer()) {
                    sender.sendMessage(ChestPrefix.PREFIX+"§cKonnte den User nicht in der Datenbank finden!");
                    return;
                }

                sender.sendMessage(ChestPrefix.PREFIX+"§eRang von §7"+name+"§e: "+
                        Rank.getRankById(databasePlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt()).getColor()+
                        Rank.getRankById(databasePlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt()).getName());
                return;
            }
        }

        if(args.length == 3) {
            if(args[0].equalsIgnoreCase("set")) {
                String name = args[1];
                UUID uuid = UUIDFetcher.getUUID(name);
                DatabasePlayer databasePlayer = new DatabasePlayer(gameChest.getDatabaseManager(), uuid);
                String rankStr = args[2];

                if(!Rank.existsRank(rankStr)) {
                    sender.sendMessage(ChestPrefix.PREFIX+"§cDieser Rang existiert nicht! - /rank list");
                    return;
                }
                Rank rank = Rank.valueOf(rankStr.toUpperCase());
                if(!databasePlayer.existsPlayer()) {
                    sender.sendMessage(ChestPrefix.PREFIX+"§cKonnte den User nicht in der Datenbank finden!");
                    return;
                }
                if(rank == Rank.PREMIUM) {
                    sender.sendMessage(ChestPrefix.PREFIX+"§cPremium-Rang -> /premium [Spielername] (Monate)");
                    return;
                }

                int rid = rank.getId();
                databasePlayer.setDatabaseObject(DatabasePlayerObject.RANK_ID, rid);

                sender.sendMessage(ChestPrefix.PREFIX+"§aRang geändert!");
                sender.sendMessage("§8\u00BB §eRang von §7"+name+"§e: "+
                        Rank.getRankById(databasePlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt()).getColor()+
                        Rank.getRankById(databasePlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt()).getName());
                if(gameChest.getProxy().getPlayer(name) != null) {
                    gameChest.getProxy().getPlayer(name).sendMessage(
                            ChestPrefix.PREFIX+"§aDein Rang wurde geändert!");
                    gameChest.getProxy().getPlayer(name).sendMessage(
                            "§8\u00BB §eDein neuer Rang ist nun: "+rank.getColor()+rank.getName());
                }
                return;
            }
        }
        sender.sendMessage(ChestPrefix.PREFIX+"§c/rank <list/info/set> [Spielername] (Rang)");
    }
}
