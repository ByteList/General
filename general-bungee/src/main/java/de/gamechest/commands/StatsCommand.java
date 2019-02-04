package de.gamechest.commands;

import de.gamechest.GameChest;
import de.gamechest.UUIDFetcher;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.database.DatabaseManager;
import de.gamechest.common.Rank;
import de.gamechest.database.stats.clickattack.DatabaseClickAttack;
import de.gamechest.database.stats.clickattack.DatabaseClickAttackObject;
import de.gamechest.database.stats.deathrun.DatabaseDeathRun;
import de.gamechest.database.stats.deathrun.DatabaseDeathRunObject;
import de.gamechest.database.stats.shulkerdefence.DatabaseShulkerDefence;
import de.gamechest.database.stats.shulkerdefence.DatabaseShulkerDefenceObject;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/**
 * Created by ByteList on 21.11.2016.
 */
public class StatsCommand extends GCCommand {

    private GameChest gameChest = GameChest.getInstance();
    private DatabaseManager databaseManager = gameChest.getDatabaseManager();

    public StatsCommand() {
        super("stats");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage("Nur als Player nutzbar!");
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        UUID uuid = player.getUniqueId();

        if(args.length == 1) {

            if(args[0].equalsIgnoreCase("ca") || args[0].equalsIgnoreCase("clickattack")) {
                sendClickAttackStats(sender, player.getName(), uuid);
                return;
            }

            if(args[0].equalsIgnoreCase("sd") || args[0].equalsIgnoreCase("shulkerdefence")) {
                sendShulkerDefenceStats(sender, player.getName(), uuid);
                return;
            }

//            if(args[0].equalsIgnoreCase("jd") || args[0].equalsIgnoreCase("jumpduell")) {
//                DatabaseJumpDuell database = databaseManager.getDatabaseJumpDuell();
//                if(!database.existsPlayer(uuid)) {
//                    sender.sendMessage(gameChest.pr_stats+"§cDu hast noch keine JumpDuell Runde gespielt.");
//                    return;
//                }
//                int rank = database.getDatabaseElement(uuid, DatabaseJumpDuellObject.RANK).getAsInt();
//                int points = database.getDatabaseElement(uuid, DatabaseJumpDuellObject.POINTS).getAsInt();
//                int played = database.getDatabaseElement(uuid, DatabaseJumpDuellObject.GAMES).getAsInt();
//                int wins = database.getDatabaseElement(uuid, DatabaseJumpDuellObject.WINS).getAsInt();
//                int fails = database.getDatabaseElement(uuid, DatabaseJumpDuellObject.FAILS).getAsInt();
//                int one_duell = database.getDatabaseElement(uuid, DatabaseJumpDuellObject.ONE_DUELL).getAsInt();
//                int triple_duell = database.getDatabaseElement(uuid, DatabaseJumpDuellObject.TRIPLE_DUELL).getAsInt();
//                int alone = database.getDatabaseElement(uuid, DatabaseJumpDuellObject.ALONE).getAsInt();
//                int coins = database.getDatabaseElement(uuid, DatabaseJumpDuellObject.EARNED_COINS).getAsInt();
//
//
//                sender.sendMessage(gameChest.pr_stats+"§eDeine JumpDuell Statistik:");
//                sender.sendMessage("§8\u00BB §7Position im Ranking: §6"+rank);
//                sender.sendMessage("§8\u00BB §7Punkte: §6"+points);
//                sender.sendMessage("§8\u00BB §7Gespielte Parkours: §6"+played);
//                sender.sendMessage("§8\u00BB §7Fails: §6"+fails+"§7 / Gewonnen: §6"+wins+"§7");
//                sender.sendMessage("§8\u00BB §7One-Duell: §6"+one_duell+"§7 / Triple-Duell: §6"+triple_duell);
//                sender.sendMessage("§8\u00BB §7Normal-Parcours: §6"+alone);
//                sender.sendMessage("§8\u00BB §7Coins: §6"+coins);
//                return;
//            }
            if(args[0].equalsIgnoreCase("dr") || args[0].equalsIgnoreCase("deathrun")) {
                sendDeathRunStats(sender, player.getName(), uuid);
                return;
            }


        }

        if(args.length == 2) {
            if(!gameChest.hasRank(uuid, Rank.SUPPORTER)) {
                gameChest.sendNoPermissionMessage(sender);
                return;
            }
            String name = args[1];
            uuid = UUIDFetcher.getUUID(name);

            if(args[0].equalsIgnoreCase("ca") || args[0].equalsIgnoreCase("clickattack")) {
                sendClickAttackStats(sender, name, uuid);
                return;
            }

            if(args[0].equalsIgnoreCase("sd") || args[0].equalsIgnoreCase("shulkerdefence")) {
                sendShulkerDefenceStats(sender, name, uuid);
                return;
            }

//            if(args[0].equalsIgnoreCase("jd") || args[0].equalsIgnoreCase("jumpduell")) {
////                if(value.startsWith("#")) {
////                    Integer rank = -1;
////                    try {
////                        rank = Integer.valueOf(value.replace("#", ""));
////                    } catch (Exception e) {
////                        sender.sendMessage(gameChest.pr_stats + "§c[#Rang] muss eine Zahl sein §7(zB: #1)");
////                        return;
////                    }
////                    if(rank > 0) {
////                        if (!Stats.getJumpDuell().existsRank(rank)) {
////                            sender.sendMessage(gameChest.pr_stats + "§cDieser Platz ist nicht vergeben!");
////                            return;
////                        }
////                        uuid = Stats.getJumpDuell().getUuidFromRank(rank);
////                        name = gameChest.getSqlHandler().getLastName("UUID", uuid.toString());
////                    } else {
////                        sender.sendMessage(gameChest.pr_stats+"§cRang muss größer als 0 sein!");
////                        return;
////                    }
////                } else {
//                    uuid = UUIDFetcher.getUUID(name);
////                }
//
//                DatabaseJumpDuell database = databaseManager.getDatabaseJumpDuell();
//                if(!database.existsPlayer(uuid)) {
//                    sender.sendMessage(gameChest.pr_stats+"§cDu hast noch keine JumpDuell Runde gespielt.");
//                    return;
//                }
//                int rank = database.getDatabaseElement(uuid, DatabaseJumpDuellObject.RANK).getAsInt();
//                int points = database.getDatabaseElement(uuid, DatabaseJumpDuellObject.POINTS).getAsInt();
//                int played = database.getDatabaseElement(uuid, DatabaseJumpDuellObject.GAMES).getAsInt();
//                int wins = database.getDatabaseElement(uuid, DatabaseJumpDuellObject.WINS).getAsInt();
//                int fails = database.getDatabaseElement(uuid, DatabaseJumpDuellObject.FAILS).getAsInt();
//                int one_duell = database.getDatabaseElement(uuid, DatabaseJumpDuellObject.ONE_DUELL).getAsInt();
//                int triple_duell = database.getDatabaseElement(uuid, DatabaseJumpDuellObject.TRIPLE_DUELL).getAsInt();
//                int alone = database.getDatabaseElement(uuid, DatabaseJumpDuellObject.ALONE).getAsInt();
//                int coins = database.getDatabaseElement(uuid, DatabaseJumpDuellObject.EARNED_COINS).getAsInt();
//
//
//                sender.sendMessage(gameChest.pr_stats+"§eJumpDuell Statistik von "+name+":");
//                sender.sendMessage("§8\u00BB §7Position im Ranking: §6"+rank);
//                sender.sendMessage("§8\u00BB §7Punkte: §6"+points);
//                sender.sendMessage("§8\u00BB §7Gespielte Parkours: §6"+played);
//                sender.sendMessage("§8\u00BB §7Fails: §6"+fails+"§7 / Gewonnen: §6"+wins+"§7");
//                sender.sendMessage("§8\u00BB §7Einzelmatches: §6"+one_duell+"§7 / Triplematches: §6"+triple_duell);
//                sender.sendMessage("§8\u00BB §7Normale Parcours: §6"+alone);
//                sender.sendMessage("§8\u00BB §7Coins: §6"+coins);
//                return;
//            }

            if(args[0].equalsIgnoreCase("dr") || args[0].equalsIgnoreCase("deathrun")) {
                sendDeathRunStats(sender, name, uuid);
                return;
            }
        }

        if(gameChest.hasRank(uuid, Rank.SUPPORTER))
            sender.sendMessage(gameChest.pr_stats+"§c/stats <ca/sd/dr> (Spieler)");
        else sender.sendMessage(gameChest.pr_stats+"§c/stats <ca/sd/dr>");
    }

    private void sendClickAttackStats(CommandSender sender, String name, UUID uuid) {
        DatabaseClickAttack database = databaseManager.getDatabaseClickAttack();
        if(!database.existsPlayer(uuid)) {
            sender.sendMessage(gameChest.pr_stats+"§c"+(sender.getName().equalsIgnoreCase(name) ? "Du hast" : name+" hat")+" noch keine ClickAttack Runde gespielt.");
            return;
        }
        int rank = database.getDatabaseElement(uuid, DatabaseClickAttackObject.RANK).getAsInt();
        int points = database.getDatabaseElement(uuid, DatabaseClickAttackObject.POINTS).getAsInt();
        int played = database.getDatabaseElement(uuid, DatabaseClickAttackObject.GAMES).getAsInt();
        int wins = database.getDatabaseElement(uuid, DatabaseClickAttackObject.WINS).getAsInt();
        int loses = played-wins;
        int kills = database.getDatabaseElement(uuid, DatabaseClickAttackObject.KILLS).getAsInt();
        int deaths = database.getDatabaseElement(uuid, DatabaseClickAttackObject.DEATHS).getAsInt();
        int clicked_blocks = database.getDatabaseElement(uuid, DatabaseClickAttackObject.CLICKED_BLOCKS).getAsInt();
        int coins = database.getDatabaseElement(uuid, DatabaseClickAttackObject.EARNED_COINS).getAsInt();

        double KD;
        String kd;

        try {
            KD = (double) kills / (double) deaths;
            kd = (Double.valueOf(KD).toString().length() < 4)
                    ? Double.valueOf(KD).toString()
                    : Double.valueOf(KD).toString().trim().substring(0, 4);
        } catch (ArithmeticException e) {
            kd = "0.0";
        }

        sender.sendMessage(gameChest.pr_stats+"§eClickAttack Statistik von "+name+":");
        sender.sendMessage("§8\u00BB §7Position im Ranking: §6"+rank);
        sender.sendMessage("§8\u00BB §7Punkte: §6"+points);
        sender.sendMessage("§8\u00BB §7Spiele: §6"+played+"§7 (Gewonnen: §6"+wins+"§7 / Verloren: §6"+loses+"§7)");
        sender.sendMessage("§8\u00BB §7Kills: §6"+kills+"§7 / Tode: §6"+deaths+"§7 - " +
                "K/D: §6"+kd.replace("Infi", "0.0").replace("NaN", "0.0"));
        sender.sendMessage("§8\u00BB §7Benutzte Blöcke: §6"+clicked_blocks);
        sender.sendMessage("§8\u00BB §7Coins: §6"+coins);
    }

    private void sendDeathRunStats(CommandSender sender, String name, UUID uuid) {
        DatabaseDeathRun database = databaseManager.getDatabaseDeathRun();
        if(!database.existsPlayer(uuid)) {
            sender.sendMessage(gameChest.pr_stats+"§c"+(sender.getName().equalsIgnoreCase(name) ? "Du hast" : name+" hat")+" noch keine DeathRun Runde gespielt.");
            return;
        }
        int rank = database.getDatabaseElement(uuid, DatabaseDeathRunObject.RANK).getAsInt();
        int points = database.getDatabaseElement(uuid, DatabaseDeathRunObject.POINTS).getAsInt();
        int played = database.getDatabaseElement(uuid, DatabaseDeathRunObject.GAMES).getAsInt();
        int wins = database.getDatabaseElement(uuid, DatabaseDeathRunObject.WINS).getAsInt();
        int loses = played+wins;
        int coins = database.getDatabaseElement(uuid, DatabaseDeathRunObject.EARNED_COINS).getAsInt();
        int useddj = database.getDatabaseElement(uuid, DatabaseDeathRunObject.USED_DOUBLE_JUMPS).getAsInt();
        int useditems = database.getDatabaseElement(uuid, DatabaseDeathRunObject.USED_ITEMS).getAsInt();


        sender.sendMessage(gameChest.pr_stats+"§eDeathRun Statistik von "+name+":");
        sender.sendMessage("§8\u00BB §7Position im Ranking: §6"+rank);
        sender.sendMessage("§8\u00BB §7Punkte: §6"+points);
        sender.sendMessage("§8\u00BB §7Spiele: §6"+played+"§7 (Gewonnen: §6"+wins+"§7 / Verloren: §6"+loses+"§7)");
        sender.sendMessage("§8\u00BB §7Benutze Doppelsprünge: §6"+useddj);
        sender.sendMessage("§8\u00BB §7Benutze Items: §6"+useditems);
        sender.sendMessage("§8\u00BB §7Coins: §6"+coins);
    }

    private void sendShulkerDefenceStats(CommandSender sender, String name, UUID uuid) {
        DatabaseShulkerDefence database = databaseManager.getDatabaseShulkerDefence();
        if(!database.existsPlayer(uuid)) {
            sender.sendMessage(gameChest.pr_stats+"§c"+(sender.getName().equalsIgnoreCase(name) ? "Du hast" : name+" hat")+" noch keine ShulkerDefence Runde gespielt.");
            return;
        }
        int rank = database.getDatabaseElement(uuid, DatabaseShulkerDefenceObject.RANK).getAsInt();
        int points = database.getDatabaseElement(uuid, DatabaseShulkerDefenceObject.POINTS).getAsInt();
        int played = database.getDatabaseElement(uuid, DatabaseShulkerDefenceObject.GAMES).getAsInt();
        int wins = database.getDatabaseElement(uuid, DatabaseShulkerDefenceObject.WINS).getAsInt();
        int loses = played-wins;
        int kills = database.getDatabaseElement(uuid, DatabaseShulkerDefenceObject.KILLS).getAsInt();
        int deaths = database.getDatabaseElement(uuid, DatabaseShulkerDefenceObject.DEATHS).getAsInt();
        int killed_shulkers = database.getDatabaseElement(uuid, DatabaseShulkerDefenceObject.KILLED_SHULKERS).getAsInt();
        int coins = database.getDatabaseElement(uuid, DatabaseShulkerDefenceObject.EARNED_COINS).getAsInt();

        double KD;
        String kd;

        try {
            KD = (double) kills / (double) deaths;
            kd = (Double.valueOf(KD).toString().length() < 4)
                    ? Double.valueOf(KD).toString()
                    : Double.valueOf(KD).toString().trim().substring(0, 4);
        } catch (ArithmeticException e) {
            kd = "0.0";
        }

        sender.sendMessage(gameChest.pr_stats+"§eShulkerDefence Statistik von "+name+":");
        sender.sendMessage("§8\u00BB §7Position im Ranking: §6"+rank);
        sender.sendMessage("§8\u00BB §7Punkte: §6"+points);
        sender.sendMessage("§8\u00BB §7Spiele: §6"+played+"§7 (Gewonnen: §6"+wins+"§7 / Verloren: §6"+loses+"§7)");
        sender.sendMessage("§8\u00BB §7Kills: §6"+kills+"§7 / Tode: §6"+deaths+"§7 - " +
                "K/D: §6"+kd.replace("Infi", "0.0").replace("NaN", "0.0"));
        sender.sendMessage("§8\u00BB §7Getötete Shulker: §6"+killed_shulkers);
        sender.sendMessage("§8\u00BB §7Coins: §6"+coins);
    }
}
