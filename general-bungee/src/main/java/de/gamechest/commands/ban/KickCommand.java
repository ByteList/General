package de.gamechest.commands.ban;

import com.google.common.collect.ImmutableSet;
import de.gamechest.GameChest;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.common.ChestPrefix;
import de.gamechest.common.Rank;
import de.gamechest.common.UUIDFetcher;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayer;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayerObject;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ByteList on 22.02.2017.
 */
public class KickCommand extends GCCommand implements TabExecutor {

    public KickCommand() {
        super("kick");
    }

    private GameChest gameChest = GameChest.getInstance();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            if (!gameChest.hasRank(pp.getUniqueId(), Rank.BUILDER)) {
                gameChest.sendNoPermissionMessage(sender);
                return;
            }
        }

        if(args.length > 1) {
            String playername = args[0];
            String onlyStaff = "";

            if(gameChest.getNick().isNameANick(playername)) {
                onlyStaff = " §7- §eNicked as §9"+playername;
                playername = gameChest.getNick().getPlayernameFromNick(playername);
            }
            UUID uuid = UUIDFetcher.getUUID(playername);
            ProxiedPlayer tp = gameChest.getProxy().getPlayer(uuid);

            if(tp == null) {
                sender.sendMessage(ChestPrefix.PREFIX+"§cDer User ist nicht online!");
                return;
            }
            StringBuilder reason = new StringBuilder();

            for (int i = 1; i < args.length; i++) {
                reason.append(args[i]).append(" ");
            }
            reason.append("#");

            reason = new StringBuilder(reason.toString().replace(" #", ""));

            tp.disconnect("§cDu wurdest vom §6Game-ChestPrefix.de Netzwerk§c gekickt."
                    + "\n" + "\n" +
                    "§cGrund: §e"+reason/*
                    + "\n" + "\n" +
                    "§6Unser Regelwerk findest du unter: §agame-chest.de/regelwerk"*/);
            for (ProxiedPlayer player : gameChest.onlineTeam) {
                if (gameChest.hasRank(player.getUniqueId(), Rank.SUPPORTER)) {
                    player.sendMessage(ChestPrefix.PREFIX_KICK + "§a" + sender + "§7 hat §c" + playername + "§7 gekickt");
                    player.sendMessage(ChestPrefix.PREFIX_KICK + "§7Grund: §e" + reason + onlyStaff);
                }
            }
            return;
        }

        sender.sendMessage(ChestPrefix.PREFIX+"§c/kick <Spieler> [Grund]");
    }



    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            if (!gameChest.hasRank(pp.getUniqueId(), Rank.SUPPORTER)) {
                gameChest.sendNoPermissionMessage(sender);
                return new ArrayList<>();
            }
        }

        if (args.length > 1 || args.length == 0) {
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
        }
        return matches;
    }
}
