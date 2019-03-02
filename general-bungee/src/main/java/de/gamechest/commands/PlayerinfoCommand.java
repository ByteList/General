package de.gamechest.commands;

import de.gamechest.GameChest;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.common.ChestPrefix;
import de.gamechest.common.Rank;
import de.gamechest.common.UUIDFetcher;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayer;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayerObject;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/**
 * Created by ByteList on 06.03.2017.
 */
public class PlayerinfoCommand extends GCCommand {

    public PlayerinfoCommand() {
        super("playerinfo", "pinfo", "playeri", "pi");
    }

    private GameChest gameChest = GameChest.getInstance();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer))
            sender.sendMessage("§cDu bist die Konsole und kannst diesen Befehl nicht ausführen!");
        else {
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            if (!gameChest.hasRank(pp.getUniqueId(), Rank.SUPPORTER)) {
                gameChest.sendNoPermissionMessage(sender);
                return;
            }

            if(args.length == 1) {
                UUID uuid = UUIDFetcher.getUUID(args[0]);
                if(uuid == null) {
                    sender.sendMessage(ChestPrefix.PREFIX+"§cKonnte den User nicht in der Datenbank finden!");
                    return;
                }
                gameChest.getDatabaseManager().getAsync().getPlayer(uuid, dbPlayer -> {
                    String playername = args[0];
                    DatabaseOnlinePlayer databaseOnlinePlayer = new DatabaseOnlinePlayer(gameChest.getDatabaseManager(), uuid.toString(), playername);

                    playername = dbPlayer.getDatabaseElement(DatabasePlayerObject.LAST_NAME).getAsString();
                    Rank rank = Rank.getRankById(dbPlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt());

                    pp.sendMessage(ChestPrefix.PREFIX+"§6Informationen über §c"+ playername +"§6:");
                    pp.sendMessage("§8\u00BB §7Id: §a"+dbPlayer.getDatabaseElement(DatabasePlayerObject.UUID).getAsString());
                    pp.sendMessage("§8\u00BB §7First-Login: §2"+dbPlayer.getDatabaseElement(DatabasePlayerObject.FIRST_LOGIN).getAsString());
                    pp.sendMessage("§8\u00BB §7Last-Login: §a"+dbPlayer.getDatabaseElement(DatabasePlayerObject.LAST_LOGIN).getAsString());
                    pp.sendMessage("§8\u00BB §7Online: "+(databaseOnlinePlayer.isOnline() ? "§aJa §6auf §e"+
                            databaseOnlinePlayer.getDatabaseElement(DatabaseOnlinePlayerObject.SERVER_ID).getAsString() : "§cNein"));
                    pp.sendMessage("§8\u00BB §7Rang: "+rank.getColor()+rank.getName());
                    pp.sendMessage("§8\u00BB §7Coins: §6"+dbPlayer.getDatabaseElement(DatabasePlayerObject.COINS).getAsInt());
                    if(gameChest.hasRank(pp.getUniqueId(), Rank.MODERATOR)) {
                        pp.sendMessage("§8\u00BB §7Letzte IP: §c"+dbPlayer.getDatabaseElement(DatabasePlayerObject.LAST_IP).getAsString());
                        if(gameChest.getNick().isNicked(uuid))
                            pp.sendMessage("§8\u00BB §7Aktuell genickt als: §9" + databaseOnlinePlayer.getDatabaseElement(DatabaseOnlinePlayerObject.NICKNAME).getAsString());
                        if(dbPlayer.getDatabaseElement(DatabasePlayerObject.OPERATOR).getAsBoolean())
                            pp.sendMessage("§4\u00BB Achtung: §cAls Operator in der Datenbank festgelegt!");

                    }
                    pp.sendMessage("");
                });
                return;
            }
            pp.sendMessage(ChestPrefix.PREFIX+"§c/playerinfo <Spieler>");
        }
    }
}
