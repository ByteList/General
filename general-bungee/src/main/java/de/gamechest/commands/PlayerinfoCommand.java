package de.gamechest.commands;

import de.bytelist.bytecloud.bungee.ByteCloudMaster;
import de.gamechest.GameChest;
import de.gamechest.UUIDFetcher;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.database.DatabasePlayer;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayer;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayerObject;
import de.gamechest.database.rank.Rank;
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
            sender.sendMessage(ByteCloudMaster.getInstance().prefix+"§cDu bist die Konsole und kannst diesen Befehl nicht ausführen!");
        else {
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            if (!gameChest.hasRank(pp.getUniqueId(), Rank.SUPPORTER)) {
                pp.sendMessage(ByteCloudMaster.getInstance().prefix + "§cDu hast keine Berechtigung für diesen Befehl!");
                return;
            }

            if(args.length == 1) {
                String playername = args[0];
                UUID uuid = UUIDFetcher.getUUID(playername);
                DatabasePlayer databasePlayer = gameChest.getDatabaseManager().getDatabasePlayer(uuid);
                DatabaseOnlinePlayer databaseOnlinePlayer = gameChest.getDatabaseManager().getDatabaseOnlinePlayer(uuid);

                if(!databasePlayer.existsPlayer()) {
                    sender.sendMessage(gameChest.prefix+"§cKonnte den User nicht in der Datenbank finden!");
                    return;
                }

                playername = databaseOnlinePlayer.getDatabaseElement(DatabaseOnlinePlayerObject.NAME).getAsString();
                Rank rank = Rank.getRankById(databasePlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt());

                pp.sendMessage(gameChest.prefix+"§6Informationen über §c"+playername+"§6:");
                pp.sendMessage("§8\u00BB §7Id: §a"+databasePlayer.getDatabaseElement(DatabasePlayerObject.UUID).getAsString());
                pp.sendMessage("§8\u00BB §7First-Login: §2"+databasePlayer.getDatabaseElement(DatabasePlayerObject.FIRST_LOGIN).getAsString());
                pp.sendMessage("§8\u00BB §7Last-Login: §a"+databasePlayer.getDatabaseElement(DatabasePlayerObject.LAST_LOGIN).getAsString());
                pp.sendMessage("§8\u00BB §7Online: "+(databaseOnlinePlayer.isOnline() ? "§aJa §6auf §e"+
                        databaseOnlinePlayer.getDatabaseElement(DatabaseOnlinePlayerObject.SERVER_ID).getAsString() : "§cNein"));
                pp.sendMessage("§8\u00BB §7Rang: "+rank.getColor()+rank.getName());
                pp.sendMessage("§8\u00BB §7Coins: §6"+databasePlayer.getDatabaseElement(DatabasePlayerObject.COINS).getAsInt());
                if(gameChest.hasRank(pp.getUniqueId(), Rank.MODERATOR)) {
                    pp.sendMessage("§8\u00BB §7Letzte IP: §c"+databasePlayer.getDatabaseElement(DatabasePlayerObject.LAST_IP).getAsString());
                    if(gameChest.getNick().isNicked(uuid))
                        pp.sendMessage("§8\u00BB §7Aktuell genickt als: §9" + databaseOnlinePlayer.getDatabaseElement(DatabaseOnlinePlayerObject.NICKNAME).getAsString());
                    if(databaseOnlinePlayer.getDatabaseElement(DatabaseOnlinePlayerObject.TOGGLED_RANK).getAsBoolean())
                        pp.sendMessage("§8\u00BB §7Aktuell getoggled!");
                    if(databasePlayer.getDatabaseElement(DatabasePlayerObject.OPERATOR).getAsBoolean())
                        pp.sendMessage("§4\u00BB Achtung: §cAls Operator in der Datenbank festgelegt!");

                }
                pp.sendMessage("");
                return;
            }

            pp.sendMessage(gameChest.prefix+"§c/playerinfo <Spieler>");
        }
    }
}
