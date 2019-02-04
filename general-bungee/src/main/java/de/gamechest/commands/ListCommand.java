package de.gamechest.commands;

import de.gamechest.GameChest;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.DatabasePlayer;
import de.gamechest.common.Rank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collection;

/**
 * Created by ByteList on 20.02.2017.
 */
public class ListCommand extends GCCommand {

    public ListCommand() {
        super("list");
    }

    private GameChest gameChest = GameChest.getInstance();

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            if (!gameChest.hasRank(pp.getUniqueId(), Rank.BUILDER)) {
                gameChest.sendNoPermissionMessage(sender);
                return;
            }

            Collection<ProxiedPlayer> playerCollection = gameChest.getProxy().getPlayers();

            String players = "";

            for (ProxiedPlayer player : playerCollection) {
                DatabasePlayer databasePlayer = new DatabasePlayer(gameChest.getDatabaseManager(), player.getUniqueId());
                String color = Rank.getRankById(databasePlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt()).getColor();
                players = players + color + player.getName() + "§7 (§e" + player.getServer().getInfo().getName() + "§7), ";
            }

            String size = "sind §c" + playerCollection.size();

            if (playerCollection.size() == 1) {
                size = "ist §cein";
                players = players.replace(",", "");
            }

            sender.sendMessage(gameChest.prefix + "§7Momentan " + size + "§7 Spieler online:");
            sender.sendMessage("§8\u00BB §r" + players);
        }
    }
}
