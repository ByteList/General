package de.gamechest.commands.rank;

import de.gamechest.GameChest;
import de.gamechest.UUIDFetcher;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.DatabasePlayer;
import de.gamechest.database.premiumplayer.DatabasePremiumPlayer;
import de.gamechest.database.premiumplayer.DatabasePremiumPlayerObject;
import de.gamechest.database.rank.Rank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

/**
 * Created by ByteList on 19.11.2016.
 */
public class PremiumCommand extends GCCommand {

    private GameChest gameChest = GameChest.getInstance();

    public PremiumCommand() {
        super("premium");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage("Nur als Player nutzbar!");
            return;
        }

        ProxiedPlayer pp = (ProxiedPlayer) sender;
        DatabasePremiumPlayer databasePremiumPlayer = gameChest.getDatabaseManager().getDatabasePremiumPlayer();
        if (!gameChest.hasRank(pp.getUniqueId(), Rank.DEVELOPER)) {
            if (!databasePremiumPlayer.existsPlayer(pp.getUniqueId())) {
                pp.sendMessage(gameChest.prefix + "§cDu besitzt keinen Premium-Rang!");
            } else {
                String endDate = databasePremiumPlayer.getDatabaseElement(pp.getUniqueId(), DatabasePremiumPlayerObject.ENDING_DATE).getAsString();
                if (endDate.equalsIgnoreCase("-2")) {
                    pp.sendMessage(gameChest.prefix + "§6Du besitzt Lifetime-Premium!!!!11elf11! <3");
                } else {
                    pp.sendMessage(gameChest.prefix + "§6Dein Premium-Rang läuft noch bis zum §c" + endDate + "§6!");
                }
            }
        } else {
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("info")) {
                    String name = args[1];
                    UUID uuid = UUIDFetcher.getUUID(name);
                    DatabasePlayer databasePlayer = gameChest.getDatabaseManager().getDatabasePlayer(uuid);

                    if (!databasePlayer.existsPlayer()) {
                        sender.sendMessage(gameChest.prefix + "§cKonnte den User nicht in der Datenbank finden!");
                        return;
                    }

                    if (!databasePremiumPlayer.existsPlayer(uuid)) {
                        sender.sendMessage(gameChest.prefix + "§cDer User besitzt keinen Premium-Rang!");
                        return;
                    }
                    String endDate = databasePremiumPlayer.getDatabaseElement(pp.getUniqueId(), DatabasePremiumPlayerObject.ENDING_DATE).getAsString();

                    if (endDate.equalsIgnoreCase("-2"))
                        sender.sendMessage(gameChest.prefix + "§eDer Premium-Rang von §6" + name + "§e läuft lebenslang!");
                    else
                        sender.sendMessage(gameChest.prefix + "§eDer Premium-Rang von §6" + name + "§e läuft bis zum: §c" + endDate);
                    return;
                }

            }

            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("set")) {
                    String name = args[1];
                    Integer value;

                    try {
                        value = Integer.valueOf(args[2]);
                    } catch (NumberFormatException ex) {
                        sender.sendMessage(gameChest.prefix + "§c(Monate) = Zahl");
                        return;
                    }
                    UUID uuid = UUIDFetcher.getUUID(name);
                    DatabasePlayer databasePlayer = gameChest.getDatabaseManager().getDatabasePlayer(uuid);
                    if (!databasePlayer.existsPlayer()) {
                        sender.sendMessage(gameChest.prefix + "§cKonnte den User nicht in der Datenbank finden!");
                        return;
                    }

                    String dateformat;
                    if (value != -2) {
                        Calendar now = Calendar.getInstance();

                        now.add(Calendar.MONTH, value);

                        dateformat = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(now.getTime());
                    } else {
                        dateformat = String.valueOf(value);
                    }

                    databasePremiumPlayer.setDatabaseObject(uuid, DatabasePremiumPlayerObject.ENDING_DATE, dateformat);
                    gameChest.getDatabaseManager().getDatabasePlayer(uuid).setDatabaseObject(DatabasePlayerObject.RANK_ID, 7);

                    sender.sendMessage(gameChest.prefix + "§aPremium hinzugefügt!");
                    if (dateformat.equalsIgnoreCase("-2")) {
                        sender.sendMessage("§8\u00BB §eDer Premium-Rang von §6" + name + "§e läuft nun lebenslang!");
                        if (gameChest.getDatabaseManager().getDatabaseOnlinePlayer(uuid).isOnline()) {
                            gameChest.getProxy().getPlayer(name).sendMessage(
                                    gameChest.prefix + "§aDu hast soeben deinen Premium-Rang erhalten!");
                            gameChest.getProxy().getPlayer(name).sendMessage(
                                    "§8\u00BB §bBitte verbinde dich neu, damit du alle deine neuen Features nutzen kannst!");
                            gameChest.getProxy().getPlayer(name).sendMessage(
                                    "§8\u00BB §eDein Premium-Rang läuft nun lebenslang!");
                        }
                    } else {
                        sender.sendMessage("§8\u00BB §eDer Premium-Rang von §6" + name + "§e läuft nun bis zum: §c" + dateformat);
                        if (gameChest.getDatabaseManager().getDatabaseOnlinePlayer(uuid).isOnline()) {
                            gameChest.getProxy().getPlayer(name).sendMessage(
                                    gameChest.prefix + "§aDu hast soeben den Premium-Rang erhalten!");
                            gameChest.getProxy().getPlayer(name).sendMessage(
                                    "§8\u00BB §bBitte verbinde dich neu, damit du alle deine neuen Features nutzen kannst!");
                            gameChest.getProxy().getPlayer(name).sendMessage(
                                    "§8\u00BB §eDein Premium-Rang läuft nun bis zum: §c" + dateformat);
                        }
                    }
                    return;
                }
            }
            sender.sendMessage(gameChest.prefix + "§c/premium <info/set> [Spielername] [Monate|-2 = Lifetime]");
        }
    }
}
