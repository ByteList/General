package de.gamechest.commands.rank;

import de.gamechest.GameChest;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.common.ChestPrefix;
import de.gamechest.common.Rank;
import de.gamechest.common.UUIDFetcher;
import de.gamechest.database.DatabasePlayer;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.premiumplayer.DatabasePremiumPlayer;
import de.gamechest.database.premiumplayer.DatabasePremiumPlayerObject;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by ByteList on 19.11.2016.
 *
 * Copyright by ByteList - https://bytelist.de/
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
                pp.sendMessage(ChestPrefix.PREFIX + "§cDu besitzt keinen Premium-Rang!");
            } else {
                String dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(
                        new Date(databasePremiumPlayer.getDatabaseElement(pp.getUniqueId(), DatabasePremiumPlayerObject.ENDING_DATE).getAsLong()));
                if (dateFormat.equals("-2")) {
                    pp.sendMessage(ChestPrefix.PREFIX + "§6Du besitzt Lifetime-Premium!!!!11elf11! <3");
                } else {
                    pp.sendMessage(ChestPrefix.PREFIX + "§6Dein Premium-Rang läuft noch bis zum §c" + dateFormat + "§6!");
                }
            }
        } else {
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("info")) {
                    String name = args[1];
                    UUID uuid = UUIDFetcher.getUUID(name);
                    DatabasePlayer databasePlayer = new DatabasePlayer(gameChest.getDatabaseManager(), uuid);

                    if (!databasePlayer.existsPlayer()) {
                        sender.sendMessage(ChestPrefix.PREFIX + "§cKonnte den User nicht in der Datenbank finden!");
                        return;
                    }

                    if (!databasePremiumPlayer.existsPlayer(uuid)) {
                        sender.sendMessage(ChestPrefix.PREFIX + "§cDer User besitzt keinen Premium-Rang!");
                        return;
                    }
                    String dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(
                            new Date(databasePremiumPlayer.getDatabaseElement(pp.getUniqueId(), DatabasePremiumPlayerObject.ENDING_DATE).getAsLong()));

                    if (dateFormat.equals("-2"))
                        sender.sendMessage(ChestPrefix.PREFIX + "§eDer Premium-Rang von §6" + name + "§e läuft lebenslang!");
                    else
                        sender.sendMessage(ChestPrefix.PREFIX + "§eDer Premium-Rang von §6" + name + "§e läuft bis zum: §c" + dateFormat);
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
                        sender.sendMessage(ChestPrefix.PREFIX + "§c(Monate) = Zahl");
                        return;
                    }
                    UUID uuid = UUIDFetcher.getUUID(name);
                    DatabasePlayer databasePlayer = new DatabasePlayer(gameChest.getDatabaseManager(), uuid);
                    if (!databasePlayer.existsPlayer()) {
                        sender.sendMessage(ChestPrefix.PREFIX + "§cKonnte den User nicht in der Datenbank finden!");
                        return;
                    }

                    long end;
                    if (value != -2) {
                        Calendar now = Calendar.getInstance();

                        now.add(Calendar.MONTH, value);

                        end = now.getTime().getTime();
                    } else {
                        end = value;
                    }
                    if(!databasePremiumPlayer.existsPlayer(uuid)) databasePremiumPlayer.createPlayer(uuid, end);
                    else databasePremiumPlayer.setDatabaseObject(uuid, DatabasePremiumPlayerObject.ENDING_DATE, end);

                    gameChest.getDatabaseManager().getAsync().getPlayer(uuid, dbPlayer-> dbPlayer.setDatabaseObject(DatabasePlayerObject.RANK_ID, 7), DatabasePlayerObject.RANK_ID);

                    sender.sendMessage(ChestPrefix.PREFIX + "§aPremium hinzugefügt!");
                    if (end == -2) {
                        sender.sendMessage("§8\u00BB §eDer Premium-Rang von §6" + name + "§e läuft nun lebenslang!");
                        if (gameChest.getProxy().getPlayer(uuid) != null) {
                            gameChest.getProxy().getPlayer(name).sendMessage(
                                    ChestPrefix.PREFIX + "§aDu hast soeben deinen Premium-Rang erhalten!");
                            gameChest.getProxy().getPlayer(name).sendMessage(
                                    "§8\u00BB §bBitte verbinde dich neu, damit du alle Features nutzen kannst!");
                            gameChest.getProxy().getPlayer(name).sendMessage(
                                    "§8\u00BB §eDein Premium-Rang läuft nun lebenslang!");
                        }
                    } else {
                        String dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date(end));
                        sender.sendMessage("§8\u00BB §eDer Premium-Rang von §6" + name + "§e läuft nun bis zum: §c" + dateFormat);
                        if (gameChest.getProxy().getPlayer(uuid) != null) {
                            gameChest.getProxy().getPlayer(name).sendMessage(
                                    ChestPrefix.PREFIX + "§aDu hast soeben den Premium-Rang erhalten!");
                            gameChest.getProxy().getPlayer(name).sendMessage(
                                    "§8\u00BB §bBitte verbinde dich neu, damit du alle Features nutzen kannst!");
                            gameChest.getProxy().getPlayer(name).sendMessage(
                                    "§8\u00BB §eDein Premium-Rang läuft nun bis zum: §c" + dateFormat);
                        }
                    }
                    return;
                }
            }
            sender.sendMessage(ChestPrefix.PREFIX + "§c/premium <info/set> [Spielername] [Monate|-2 = Lifetime]");
        }
    }
}
