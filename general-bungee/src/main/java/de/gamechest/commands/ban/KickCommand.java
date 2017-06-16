package de.gamechest.commands.ban;

import com.google.common.collect.ImmutableSet;
import de.gamechest.GameChest;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayer;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayerObject;
import de.gamechest.database.rank.Rank;
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
            if (!gameChest.hasRank(pp.getUniqueId(), Rank.SUPPORTER)) {
                sender.sendMessage(gameChest.prefix + "§cDu hast keine Berechtigung für diesen Befehl!");
                return;
            }
        }

        if(args.length > 1) {
            String playername = args[0];
            ProxiedPlayer tp = gameChest.getProxy().getPlayer(playername);

            if(tp == null) {
                sender.sendMessage(gameChest.prefix+"§cDer User ist nicht online!");
                return;
            }

            UUID uuid = tp.getUniqueId();
            DatabaseOnlinePlayer databaseOnlinePlayer = gameChest.getDatabaseManager().getDatabaseOnlinePlayer(uuid);

            playername = databaseOnlinePlayer.getDatabaseElement(DatabaseOnlinePlayerObject.NAME).getAsString();
            String reason = "";

            for (int i = 1; i < args.length; i++) {
                reason = reason + args[i] + " ";
            }
            reason += "#";

            reason = reason.replace(" #", "");

            tp.disconnect("§cDu wurdest vom §6Game-Chest.de Netzwerk§c gekickt."
                    + "\n" + "\n" +
                    "§cGrund: §e"+reason/*
                    + "\n" + "\n" +
                    "§6Unser Regelwerk findest du unter: §agame-chest.de/regelwerk"*/);
            for (ProxiedPlayer player : gameChest.getProxy().getPlayers()) {
                if (gameChest.hasRank(player.getUniqueId(), Rank.SUPPORTER)) {
                    player.sendMessage(gameChest.pr_kick + "§a" + sender + "§7 hat §c" + playername + "§7 gekickt");
                    player.sendMessage(gameChest.pr_kick + "§7Grund: §e" + reason);
                }
            }
            return;
        }

        sender.sendMessage(gameChest.prefix+"§c/kick <Spieler> [Grund]");
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
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                if(gameChest.getNick().isNicked(player.getUniqueId())) {
                    DatabaseOnlinePlayer databaseOnlinePlayer = gameChest.getDatabaseManager().getDatabaseOnlinePlayer(player.getUniqueId());
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
