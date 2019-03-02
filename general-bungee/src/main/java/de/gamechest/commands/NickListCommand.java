package de.gamechest.commands;

import de.gamechest.GameChest;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.common.ChestPrefix;
import de.gamechest.common.Rank;
import de.gamechest.common.UUIDFetcher;
import de.gamechest.database.DatabasePlayerObject;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/**
 * Created by ByteList on 21.11.2016.
 */
public class NickListCommand extends GCCommand {

    private GameChest gameChest = GameChest.getInstance();

    public NickListCommand() {
        super("nicklist");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage("Nur als Player nutzbar!");
            return;
        }

        ProxiedPlayer pp = (ProxiedPlayer) sender;
        if (!gameChest.hasRank(pp.getUniqueId(), Rank.SUPPORTER)) {
            gameChest.sendNoPermissionMessage(sender);
        } else {
            if (gameChest.getNick().getNickedPlayers().isEmpty()) {
                sender.sendMessage(ChestPrefix.PREFIX_NICK + "§aMomentan ist niemand genickt.");
                return;
            }

            sender.sendMessage(ChestPrefix.PREFIX_NICK + "§7Momentan genickte User:");
            for (String name : gameChest.getNick().getNickedPlayers()) {
                UUID uuid = UUIDFetcher.getUUID(name);
                gameChest.getDatabaseManager().getAsync().getPlayer(uuid, dbPlayer -> {
                    String nick = gameChest.getNick().getNick(uuid);
                    String rank = Rank.getRankById(dbPlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt()).getName();

                    sender.sendMessage("§8\u00BB §e" + name + "§7 (" + rank + ") §cspielt als §b" + nick);
                }, DatabasePlayerObject.RANK_ID);
            }
        }
    }
}
