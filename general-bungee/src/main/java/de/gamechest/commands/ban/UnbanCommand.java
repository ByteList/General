package de.gamechest.commands.ban;

import com.google.common.collect.ImmutableSet;
import de.gamechest.GameChest;
import de.gamechest.UUIDFetcher;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.DatabasePlayer;
import de.gamechest.database.ban.DatabaseBan;
import de.gamechest.database.rank.Rank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ByteList on 22.02.2017.
 */
public class UnbanCommand extends GCCommand implements TabExecutor {

    public UnbanCommand() {
        super("unban", "pardon");
    }

    private GameChest gameChest = GameChest.getInstance();
    private DatabaseBan databaseBan = gameChest.getDatabaseManager().getDatabaseBan();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            if (!gameChest.hasRank(pp.getUniqueId(), Rank.MODERATOR)) {
                sender.sendMessage(gameChest.prefix + "§cDu hast keine Berechtigung für diesen Befehl!");
                return;
            }
        }

        if(args.length == 1) {
            String playername = args[0];
            UUID uuid = UUIDFetcher.getUUID(playername);
            DatabasePlayer databasePlayer = new DatabasePlayer(gameChest.getDatabaseManager(), uuid);

            if (!databasePlayer.existsPlayer()) {
                sender.sendMessage(gameChest.prefix + "§cKonnte den User nicht in der Datenbank finden!");
                return;
            }

            playername = databasePlayer.getDatabaseElement(DatabasePlayerObject.LAST_NAME).getAsString();

            if(!databaseBan.isBanned(uuid)) {
                sender.sendMessage(gameChest.prefix+"§cDieser User ist nicht gebannt!");
                return;
            }

            databaseBan.unBan(uuid);
            for(ProxiedPlayer player : gameChest.onlineTeam) {
                if (gameChest.hasRank(player.getUniqueId(), Rank.MODERATOR)) {
                    player.sendMessage(gameChest.pr_ban + "§a" + sender + "§7 hat §c" + playername + "§7 entbannt");
                }
            }
            return;
        }

        sender.sendMessage(gameChest.prefix+"§c/unban <Spieler>");
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

        if (args.length > 1 || args.length == 0) {
            return ImmutableSet.of();
        }

        Set<String> matches = new HashSet<>();
        if (args.length == 1) {
            String search = args[0].toLowerCase();
            for (UUID uuid : databaseBan.getBannedUuids()) {
                DatabasePlayer databasePlayer = new DatabasePlayer(gameChest.getDatabaseManager(), uuid);
                String lastName = databasePlayer.getDatabaseElement(DatabasePlayerObject.LAST_NAME).getAsString();
                if (lastName.toLowerCase().startsWith(search)) {
                    matches.add(lastName);
                }
            }
        }
        return matches;
    }
}
