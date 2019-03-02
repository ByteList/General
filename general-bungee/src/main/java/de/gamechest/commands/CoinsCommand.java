package de.gamechest.commands;

import de.gamechest.GameChest;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.common.ChestPrefix;
import de.gamechest.common.Rank;
import de.gamechest.common.UUIDFetcher;
import de.gamechest.database.DatabasePlayer;
import de.gamechest.database.DatabasePlayerObject;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/**
 * Created by ByteList on 19.11.2016.
 */
public class CoinsCommand extends GCCommand {

    private GameChest gameChest = GameChest.getInstance();

    public CoinsCommand() {
        super("coins");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage("Nur als Player nutzbar!");
            return;
        }

        ProxiedPlayer pp = (ProxiedPlayer) sender;
        if(!gameChest.hasRank(pp.getUniqueId(), Rank.DEVELOPER)) {
            pp.sendMessage(ChestPrefix.PREFIX+"§eDeine Coins: §7"+new DatabasePlayer(gameChest.getDatabaseManager(), pp.getUniqueId()).getDatabaseElement(DatabasePlayerObject.COINS).getAsInt());
        } else {
            if(args.length == 2) {
                if(args[0].equalsIgnoreCase("get")) {
                    String name = args[1];
                    UUID uuid = UUIDFetcher.getUUID(name);

                    gameChest.getDatabaseManager().getAsync().getPlayer(uuid, dbPlayer-> {
                        if(!dbPlayer.existsPlayer()) {
                            sender.sendMessage(ChestPrefix.PREFIX+"§cKonnte den User nicht in der Datenbank finden!");
                            return;
                        }

                        sender.sendMessage(ChestPrefix.PREFIX+"§eCoins von "+
                                Rank.getRankById(dbPlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt()).getColor()+name+"§e: §7"
                                + dbPlayer.getDatabaseElement(DatabasePlayerObject.COINS).getAsInt());
                    }, DatabasePlayerObject.RANK_ID, DatabasePlayerObject.COINS);

                    return;
                }

                if(args[0].equalsIgnoreCase("reset")) {
                    String name = args[1];
                    UUID uuid = UUIDFetcher.getUUID(name);
                    gameChest.getDatabaseManager().getAsync().getPlayer(uuid, dbPlayer-> {
                        if(!dbPlayer.existsPlayer()) {
                            sender.sendMessage(ChestPrefix.PREFIX+"§cKonnte den User nicht in der Datenbank finden!");
                            return;
                        }

                        dbPlayer.setDatabaseObject(DatabasePlayerObject.COINS, 100);
                        sender.sendMessage(ChestPrefix.PREFIX+"§aCoins wurden resetet!");
                        sender.sendMessage("§8\u00BB §eCoins von "+
                                Rank.getRankById(dbPlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt()).getColor()+name+"§e: §7"
                                + dbPlayer.getDatabaseElement(DatabasePlayerObject.COINS).getAsInt());
                    }, DatabasePlayerObject.RANK_ID, DatabasePlayerObject.COINS);

                    return;
                }
            }

            if(args.length == 3) {
                if(args[0].equalsIgnoreCase("set")) {
                    String name = args[1];
                    Integer value;

                    try {
                        value = Integer.valueOf(args[2]);
                    } catch (NumberFormatException ex) {
                        sender.sendMessage(ChestPrefix.PREFIX+"§c(Anzahl) = Zahlen");
                        return;
                    }

                    UUID uuid = UUIDFetcher.getUUID(name);
                    gameChest.getDatabaseManager().getAsync().getPlayer(uuid, dbPlayer-> {
                        if(!dbPlayer.existsPlayer()) {
                            sender.sendMessage(ChestPrefix.PREFIX+"§cKonnte den User nicht in der Datenbank finden!");
                            return;
                        }

                        dbPlayer.setDatabaseObject(DatabasePlayerObject.COINS, value);
                        sender.sendMessage(ChestPrefix.PREFIX+"§aCoins wurden geändert!");
                        sender.sendMessage("§8\u00BB §eCoins von "+
                                Rank.getRankById(dbPlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt()).getColor()+name+"§e: §7"
                                + dbPlayer.getDatabaseElement(DatabasePlayerObject.COINS).getAsInt());
                    }, DatabasePlayerObject.RANK_ID, DatabasePlayerObject.COINS);
                    return;
                }
                if(args[0].equalsIgnoreCase("add")) {
                    String name = args[1];
                    final Integer[] value = new Integer[1];

                    try {
                        value[0] = Integer.valueOf(args[2]);
                    } catch (NumberFormatException ex) {
                        sender.sendMessage(ChestPrefix.PREFIX+"§c(Anzahl) = Zahlen");
                        return;
                    }

                    UUID uuid = UUIDFetcher.getUUID(name);
                    gameChest.getDatabaseManager().getAsync().getPlayer(uuid, dbPlayer-> {

                        if(!dbPlayer.existsPlayer()) {
                            sender.sendMessage(ChestPrefix.PREFIX+"§cKonnte den User nicht in der Datenbank finden!");
                            return;
                        }

                        value[0] = value[0] +dbPlayer.getDatabaseElement(DatabasePlayerObject.COINS).getAsInt();

                        dbPlayer.setDatabaseObject(DatabasePlayerObject.COINS, value[0]);
                        sender.sendMessage(ChestPrefix.PREFIX+"§aCoins wurden hinzugefügt!");
                        sender.sendMessage("§8\u00BB §eCoins von "+
                                Rank.getRankById(dbPlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt()).getColor()+name+"§e: §7"
                                + dbPlayer.getDatabaseElement(DatabasePlayerObject.COINS).getAsInt());
                    }, DatabasePlayerObject.RANK_ID, DatabasePlayerObject.COINS);
                    return;
                }
                if(args[0].equalsIgnoreCase("rmv")) {
                    String name = args[1];
                    final Integer[] value = new Integer[1];

                    try {
                        value[0] = Integer.valueOf(args[2]);
                    } catch (NumberFormatException ex) {
                        sender.sendMessage(ChestPrefix.PREFIX+"§c(Anzahl) = Zahlen");
                        return;
                    }

                    UUID uuid = UUIDFetcher.getUUID(name);
                    gameChest.getDatabaseManager().getAsync().getPlayer(uuid, dbPlayer-> {
                        if(!dbPlayer.existsPlayer()) {
                            sender.sendMessage(ChestPrefix.PREFIX+"§cKonnte den User nicht in der Datenbank finden!");
                            return;
                        }

                        value[0] = value[0] -dbPlayer.getDatabaseElement(DatabasePlayerObject.COINS).getAsInt();

                        dbPlayer.setDatabaseObject(DatabasePlayerObject.COINS, value[0]);
                        sender.sendMessage(ChestPrefix.PREFIX+"§aCoins wurden entfernt!");
                        sender.sendMessage("§8\u00BB §eCoins von "+
                                Rank.getRankById(dbPlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt()).getColor()+name+"§e: §7"
                                + dbPlayer.getDatabaseElement(DatabasePlayerObject.COINS).getAsInt());
                    }, DatabasePlayerObject.RANK_ID, DatabasePlayerObject.COINS);

                    return;
                }
            }
            sender.sendMessage(ChestPrefix.PREFIX+"§c/coins <get/set/add/rmv/reset> [Spielername] (Anzahl)");
        }
    }
}
