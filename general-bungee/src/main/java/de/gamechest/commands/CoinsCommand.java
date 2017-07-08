package de.gamechest.commands;

import de.gamechest.GameChest;
import de.gamechest.UUIDFetcher;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.DatabasePlayer;
import de.gamechest.database.rank.Rank;
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
            pp.sendMessage(gameChest.prefix+"§eDeine Coins: §7"+new DatabasePlayer(gameChest.getDatabaseManager(), pp.getUniqueId()).getDatabaseElement(DatabasePlayerObject.COINS).getAsInt());
        } else {
            if(args.length == 2) {
                if(args[0].equalsIgnoreCase("get")) {
                    String name = args[1];
                    UUID uuid = UUIDFetcher.getUUID(name);

                    gameChest.getDatabaseManager().getAsync().getPlayer(uuid, dbPlayer-> {
                        if(!dbPlayer.existsPlayer()) {
                            sender.sendMessage(gameChest.prefix+"§cKonnte den User nicht in der Datenbank finden!");
                            return;
                        }

                        sender.sendMessage(gameChest.prefix+"§eCoins von "+
                                Rank.getRankById(dbPlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt()).getColor()+name+"§e: §7"
                                + dbPlayer.getDatabaseElement(DatabasePlayerObject.COINS).getAsInt());
                    });

                    return;
                }

                if(args[0].equalsIgnoreCase("reset")) {
                    String name = args[1];
                    UUID uuid = UUIDFetcher.getUUID(name);
                    gameChest.getDatabaseManager().getAsync().getPlayer(uuid, dbPlayer-> {
                        if(!dbPlayer.existsPlayer()) {
                            sender.sendMessage(gameChest.prefix+"§cKonnte den User nicht in der Datenbank finden!");
                            return;
                        }

                        dbPlayer.setDatabaseObject(DatabasePlayerObject.COINS, 100);
                        sender.sendMessage(gameChest.prefix+"§aCoins wurden resetet!");
                        sender.sendMessage("§8\u00BB §eCoins von "+
                                Rank.getRankById(dbPlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt()).getColor()+name+"§e: §7"
                                + dbPlayer.getDatabaseElement(DatabasePlayerObject.COINS).getAsInt());
                    });

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
                        sender.sendMessage(gameChest.prefix+"§c(Anzahl) = Zahlen");
                        return;
                    }

                    UUID uuid = UUIDFetcher.getUUID(name);
                    gameChest.getDatabaseManager().getAsync().getPlayer(uuid, dbPlayer-> {
                        if(!dbPlayer.existsPlayer()) {
                            sender.sendMessage(gameChest.prefix+"§cKonnte den User nicht in der Datenbank finden!");
                            return;
                        }

                        dbPlayer.setDatabaseObject(DatabasePlayerObject.COINS, value);
                        sender.sendMessage(gameChest.prefix+"§aCoins wurden geändert!");
                        sender.sendMessage("§8\u00BB §eCoins von "+
                                Rank.getRankById(dbPlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt()).getColor()+name+"§e: §7"
                                + dbPlayer.getDatabaseElement(DatabasePlayerObject.COINS).getAsInt());
                    });
                    return;
                }
                if(args[0].equalsIgnoreCase("add")) {
                    String name = args[1];
                    final Integer[] value = new Integer[1];

                    try {
                        value[0] = Integer.valueOf(args[2]);
                    } catch (NumberFormatException ex) {
                        sender.sendMessage(gameChest.prefix+"§c(Anzahl) = Zahlen");
                        return;
                    }

                    UUID uuid = UUIDFetcher.getUUID(name);
                    gameChest.getDatabaseManager().getAsync().getPlayer(uuid, dbPlayer-> {

                        if(!dbPlayer.existsPlayer()) {
                            sender.sendMessage(gameChest.prefix+"§cKonnte den User nicht in der Datenbank finden!");
                            return;
                        }

                        value[0] = value[0] +dbPlayer.getDatabaseElement(DatabasePlayerObject.COINS).getAsInt();

                        dbPlayer.setDatabaseObject(DatabasePlayerObject.COINS, value[0]);
                        sender.sendMessage(gameChest.prefix+"§aCoins wurden hinzugefügt!");
                        sender.sendMessage("§8\u00BB §eCoins von "+
                                Rank.getRankById(dbPlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt()).getColor()+name+"§e: §7"
                                + dbPlayer.getDatabaseElement(DatabasePlayerObject.COINS).getAsInt());
                    });
                    return;
                }
                if(args[0].equalsIgnoreCase("rmv")) {
                    String name = args[1];
                    final Integer[] value = new Integer[1];

                    try {
                        value[0] = Integer.valueOf(args[2]);
                    } catch (NumberFormatException ex) {
                        sender.sendMessage(gameChest.prefix+"§c(Anzahl) = Zahlen");
                        return;
                    }

                    UUID uuid = UUIDFetcher.getUUID(name);
                    gameChest.getDatabaseManager().getAsync().getPlayer(uuid, dbPlayer-> {
                        if(!dbPlayer.existsPlayer()) {
                            sender.sendMessage(gameChest.prefix+"§cKonnte den User nicht in der Datenbank finden!");
                            return;
                        }

                        value[0] = value[0] -dbPlayer.getDatabaseElement(DatabasePlayerObject.COINS).getAsInt();

                        dbPlayer.setDatabaseObject(DatabasePlayerObject.COINS, value[0]);
                        sender.sendMessage(gameChest.prefix+"§aCoins wurden entfernt!");
                        sender.sendMessage("§8\u00BB §eCoins von "+
                                Rank.getRankById(dbPlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt()).getColor()+name+"§e: §7"
                                + dbPlayer.getDatabaseElement(DatabasePlayerObject.COINS).getAsInt());
                    });

                    return;
                }
            }
            sender.sendMessage(gameChest.prefix+"§c/coins <get/set/add/rmv/reset> [Spielername] (Anzahl)");
        }
    }
}
