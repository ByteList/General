package de.gamechest.commands.ban;

import com.google.common.collect.ImmutableSet;
import de.gamechest.GameChest;
import de.gamechest.UUIDFetcher;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.DatabasePlayer;
import de.gamechest.database.ban.DatabaseBan;
import de.gamechest.database.ban.DatabaseBanObject;
import de.gamechest.database.ban.Reason;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayer;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayerObject;
import de.gamechest.database.rank.Rank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.*;

/**
 * Created by ByteList on 22.02.2017.
 */
public class BanCommand extends GCCommand implements TabExecutor {

    public BanCommand() {
        super("ban");
    }

    private GameChest gameChest = GameChest.getInstance();
    private DatabaseBan databaseBan = gameChest.getDatabaseManager().getDatabaseBan();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            if (!gameChest.hasRank(pp.getUniqueId(), Rank.SUPPORTER)) {
                sender.sendMessage(gameChest.prefix + "§cDu hast keine Berechtigung für diesen Befehl!");
                return;
            }
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reasons")) {
                sender.sendMessage(gameChest.prefix + "§7Bangründe: (Grund - Zeit)");
                for (String reasons : Reason.getReasonsAsString()) {
                    Reason reason = Reason.getReason(reasons);
                    sender.sendMessage("§8\u00BB §e" + reasons + " §7- §f" + reason.getTime() + " " + reason.getValue().getName());
                }
                return;
            }
            if (args[0].equalsIgnoreCase("list")) {
                if (databaseBan.getBannedUuids().isEmpty()) {
                    sender.sendMessage(gameChest.prefix + "§aEs ist momentan niemand gebannt!");
                    return;
                }
                sender.sendMessage(gameChest.prefix + "§7Gebannte User:");
                List<UUID> uuids = databaseBan.getBannedUuids();

                StringBuilder players = new StringBuilder();

                for (UUID uuid : uuids) {
                    players.append("§6").append(
                            new DatabasePlayer(gameChest.getDatabaseManager(), uuid).getDatabaseElement(DatabasePlayerObject.LAST_NAME).getAsString())
                            .append("§7 (§e").append(databaseBan.getDatabaseElement(uuid, DatabaseBanObject.REASON).getAsString()).append("§7), ");
                }
                sender.sendMessage("§8\u00BB " + players);
                return;
            }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("info")) {
                String playername = args[1];
                UUID uuid = UUIDFetcher.getUUID(playername);
                DatabasePlayer databasePlayer = new DatabasePlayer(gameChest.getDatabaseManager(), uuid);

                if (!databasePlayer.existsPlayer()) {
                    sender.sendMessage(gameChest.prefix + "§cKonnte den User nicht in der Datenbank finden!");
                    return;
                }

                sender.sendMessage(gameChest.prefix + "§7Ban-Infos:");
                sender.sendMessage("§8\u00BB §7Spieler: " +
                        Rank.getRankById(databasePlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt()).getColor() + playername);
                sender.sendMessage("§8\u00BB §fGrund: §e" + Reason.valueOf(databaseBan.getDatabaseElement(uuid, DatabaseBanObject.REASON).getAsString()).getReason());
                if(databaseBan.getDatabaseElement(uuid, DatabaseBanObject.EXTRA_MESSAGE).getObject() != null)
                    sender.sendMessage("§8\u00BB §7Extra: §e" + databaseBan.getDatabaseElement(uuid, DatabaseBanObject.EXTRA_MESSAGE).getAsString());
                sender.sendMessage("§8\u00BB §fEndet am: §c" + databaseBan.getDatabaseElement(uuid, DatabaseBanObject.END_DATE).getAsString());
                sender.sendMessage("§8\u00BB §7Erstellt von: " + databaseBan.getDatabaseElement(uuid, DatabaseBanObject.SENDER).getAsString());
                sender.sendMessage("§8\u00BB §fErstellt am: §c" + databaseBan.getDatabaseElement(uuid, DatabaseBanObject.START_DATE).getAsString());
                return;
            }

            String playername = args[0];
            String onlyStaff = "";

            if(gameChest.getNick().isNameANick(playername)) {
                onlyStaff = " §7- §eNicked as §9"+playername;
                playername = gameChest.getNick().getPlayernameFromNick(playername);
            }
            UUID uuid = UUIDFetcher.getUUID(playername);
            DatabasePlayer databasePlayer = new DatabasePlayer(gameChest.getDatabaseManager(), uuid);

            if (!databasePlayer.existsPlayer()) {
                sender.sendMessage(gameChest.prefix + "§cKonnte den User nicht in der Datenbank finden!");
                return;
            }

            String reason = args[1];

            if (Reason.getReasonsAsString().contains(reason.toUpperCase())) {
                if (databaseBan.isBanned(uuid)) {
                    sender.sendMessage(gameChest.pr_ban + "§7Der Ban wurde geändert.");
                    onlyStaff = "§7 - §eBan edited";
                }
                databaseBan.ban(uuid, Reason.getReason(reason.toUpperCase()), null, onlyStaff, sender.getName());
                ProxiedPlayer pp = gameChest.getProxy().getPlayer(uuid);
                if(pp != null)
                    pp.disconnect(gameChest.getBanMessage(pp.getUniqueId()));
                for (ProxiedPlayer player : gameChest.getProxy().getPlayers()) {
                    if (gameChest.hasRank(player.getUniqueId(), Rank.SUPPORTER)) {
                        player.sendMessage(gameChest.pr_ban + "§a" + sender + "§7 hat §c" + playername + "§7 gebannt");
                        player.sendMessage(gameChest.pr_ban + "§7Grund: §e" + Reason.getReason(reason.toUpperCase()).name() + onlyStaff);
                    }
                }
                return;
            } else {
                sender.sendMessage(gameChest.prefix + "§cDieser Grund existiert nicht! - /ban reasons");
                return;
            }
        }

        if (args.length > 2) {
            String playername = args[0];
            String onlyStaff = "";

            if(gameChest.getNick().isNameANick(playername)) {
                onlyStaff = " §7- §eNicked as §9"+playername;
                playername = gameChest.getNick().getPlayernameFromNick(playername);
            }
            UUID uuid = UUIDFetcher.getUUID(playername);
            DatabasePlayer databasePlayer = new DatabasePlayer(gameChest.getDatabaseManager(), uuid);

            if (!databasePlayer.existsPlayer()) {
                sender.sendMessage(gameChest.prefix + "§cKonnte den User nicht in der Datenbank finden!");
                return;
            }

            String reason = args[1];
            StringBuilder extra = new StringBuilder();

            for (int i = 2; i < args.length; i++) {
                extra.append(args[i]).append(" ");
            }
            extra.append("#");

            extra = new StringBuilder(extra.toString().replace(" #", ""));

            if (Reason.getReasonsAsString().contains(reason.toUpperCase())) {
                if (databaseBan.isBanned(uuid)) {
                    sender.sendMessage(gameChest.pr_ban + "§7Der Ban wurde geändert.");
                    onlyStaff = "§7 - §eBan edited";
                }
                databaseBan.ban(uuid, Reason.getReason(reason.toUpperCase()), extra.toString(), onlyStaff, sender.getName());
                ProxiedPlayer pp = gameChest.getProxy().getPlayer(uuid);
                if(pp != null)
                    pp.disconnect(gameChest.getBanMessage(pp.getUniqueId()));
                for (ProxiedPlayer player : gameChest.getProxy().getPlayers()) {
                    if (gameChest.hasRank(player.getUniqueId(), Rank.SUPPORTER)) {
                        player.sendMessage(gameChest.pr_ban + "§a" + sender + "§7 hat §c" + playername + "§7 gebannt");
                        player.sendMessage(gameChest.pr_ban + "§7Grund: §e" + Reason.getReason(reason.toUpperCase()).toString() + " (" + extra + ")" + onlyStaff);
                    }
                }
                return;
            } else {
                sender.sendMessage(gameChest.prefix + "§cDieser Grund existiert nicht! - /ban reasons");
                return;
            }
        }

        sender.sendMessage(gameChest.prefix + "§c/ban <Spieler> [Grund] (Extra Nachricht)");
        sender.sendMessage(gameChest.prefix + "§c/ban info <Spieler>");
        sender.sendMessage(gameChest.prefix + "§c/ban list");
        sender.sendMessage(gameChest.prefix + "§c/ban reasons");
    }


    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            if (!gameChest.hasRank(pp.getUniqueId(), Rank.SUPPORTER)) {
                sender.sendMessage(gameChest.prefix + "§cDu hast keine Berechtigung für diesen Befehl!");
                return new ArrayList<>();
            }
        }

        if (args.length > 2 || args.length == 0) {
            return ImmutableSet.of();
        }

        Set<String> matches = new HashSet<>();
        if (args.length == 1) {
            String search = args[0].toLowerCase();
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                if(gameChest.getNick().isNicked(player.getUniqueId())) {
                    DatabaseOnlinePlayer databaseOnlinePlayer = new DatabaseOnlinePlayer(gameChest.getDatabaseManager(), player.getUniqueId().toString(), player.getName());
                    String nick = databaseOnlinePlayer.getDatabaseElement(DatabaseOnlinePlayerObject.NICKNAME).getAsString();
                    if(nick.toLowerCase().startsWith(search)) {
                        matches.add(nick);
                    }
                } else {
                    if (player.getName().toLowerCase().startsWith(search)) {
                        matches.add(player.getName());
                    }
                }
            }
            if ("info".startsWith(search)) {
                matches.add("info");
            }
            if ("list".startsWith(search)) {
                matches.add("list");
            }
            if ("reasons".startsWith(search)) {
                matches.add("reasons");
            }
        }

        if(args.length == 2) {
            String search = args[1].toLowerCase();

            if(args[0].equalsIgnoreCase("info")) {
                for (UUID uuid : databaseBan.getBannedUuids()) {
                    DatabasePlayer databasePlayer = new DatabasePlayer(gameChest.getDatabaseManager(), uuid);
                    String lastName = databasePlayer.getDatabaseElement(DatabasePlayerObject.LAST_NAME).getAsString();
                    if (lastName.toLowerCase().startsWith(search)) {
                        matches.add(lastName);
                    }
                }
            } else {
                for (String reason : Reason.getReasonsAsString()) {
                    if (reason.toLowerCase().startsWith(search)) {
                        matches.add(reason);
                    }
                }
            }
        }
        return matches;
    }
}
