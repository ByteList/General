package de.gamechest.commands.report.bug;

import com.google.common.collect.ImmutableSet;
import de.gamechest.GameChest;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.common.ChestPrefix;
import de.gamechest.common.Rank;
import de.gamechest.database.DatabaseManager;
import de.gamechest.database.bug.BugReason;
import de.gamechest.database.bug.BugState;
import de.gamechest.database.bug.DatabaseBugreportObject;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayerObject;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.*;

/**
 * Created by ByteList on 25.04.2017.
 */
public class BugReportCommand extends GCCommand implements TabExecutor {

    public BugReportCommand() {
        super("bugreport", "bug", "br");
    }

    private GameChest gameChest = GameChest.getInstance();
    private DatabaseManager databaseManager = gameChest.getDatabaseManager();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer pp = (ProxiedPlayer) sender;

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("waiting")) {
                    if (!gameChest.hasRank(pp.getUniqueId(), Rank.DEVELOPER)) {
                        sender.sendMessage(ChestPrefix.PREFIX + "§cDu hast keine Berechtigung für diesen Befehl!");
                        return;
                    }
                    List<String> bugIds = new ArrayList<>(databaseManager.getDatabaseBugreport().getWaitingReports());

                    if (bugIds.size() == 0) {
                        sender.sendMessage(ChestPrefix.PREFIX_BUG_REPORT + "§aEs existieren keine offenen Bugs!");
                        return;
                    }

                    String ids = "";
                    for (String bugId : bugIds) {
                        ids = ids + "§c" + bugId + " §7(§e" + BugReason.valueOf(databaseManager.getDatabaseBugreport().getDatabaseElement(bugId, DatabaseBugreportObject.REASON).getAsString()) + "§7)§8" + ", ";
                    }
                    ids = ids + "#";
                    ids = ids.replace(", #", "");

                    sender.sendMessage(ChestPrefix.PREFIX_BUG_REPORT + "§bOffene Bug-Reports: " + ids);
                    return;
                }
                if (args[0].equalsIgnoreCase("all")) {
                    if (!gameChest.hasRank(pp.getUniqueId(), Rank.DEVELOPER)) {
                        sender.sendMessage(ChestPrefix.PREFIX + "§cDu hast keine Berechtigung für diesen Befehl!");
                        return;
                    }
                    List<String> bugIds = new ArrayList<>(databaseManager.getDatabaseBugreport().getBugreportIds());

                    if (bugIds.size() == 0) {
                        sender.sendMessage(ChestPrefix.PREFIX_BUG_REPORT + "§aEs existieren keine Bugs!");
                        return;
                    }

                    StringBuilder ids = new StringBuilder();
                    for (String bugId : bugIds) {
                        ids.append("§c").append(bugId).append(" §7(§e").append(BugReason.valueOf(databaseManager.getDatabaseBugreport().getDatabaseElement(bugId, DatabaseBugreportObject.REASON).getAsString())).append("§7)§8").append(", ");
                    }
                    ids.append("#");
                    ids = new StringBuilder(ids.toString().replace(", #", ""));

                    sender.sendMessage(ChestPrefix.PREFIX_BUG_REPORT + "§bAlle Bug-Reports: " + ids);
                    return;
                }
            }

            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("info")) {
                    String bugId = args[1];

                    if (!databaseManager.getDatabaseBugreport().existsBugreport(bugId)) {
                        sender.sendMessage(ChestPrefix.PREFIX + "§cDieser Bug-Report existiert nicht!");
                        return;
                    }

                    boolean hasRank = true;

                    if (!gameChest.hasRank(pp.getUniqueId(), Rank.DEVELOPER)) {
                        hasRank = false;
                        if (!pp.getUniqueId().toString().equals(databaseManager.getDatabaseBugreport().getDatabaseElement(bugId, DatabaseBugreportObject.CREATED_BY).getAsString())) {
                            sender.sendMessage(ChestPrefix.PREFIX + "§cDieser Bug-Report wurde nicht von dir erstellt!");
                            return;
                        }
                    }

                    pp.sendMessage(ChestPrefix.PREFIX_BUG_REPORT + "§bInfo über den Bug-Report §e" + bugId + "§b:");
                    pp.sendMessage("§8\u00BB §7Grund: §a" + BugReason.valueOf(databaseManager.getDatabaseBugreport().getDatabaseElement(bugId, DatabaseBugreportObject.REASON).getAsString()).getBetterReason());
                    pp.sendMessage("§8\u00BB §7Server-ID: §a" + databaseManager.getDatabaseBugreport().getDatabaseElement(bugId, DatabaseBugreportObject.SERVER_ID).getAsString());
                    if (databaseManager.getDatabaseBugreport().getDatabaseElement(bugId, DatabaseBugreportObject.PREVIOUS_SERVER_ID).getObject() != null)
                        pp.sendMessage("§8\u00BB §7Vorherige Server-ID: §e" + databaseManager.getDatabaseBugreport().getDatabaseElement(bugId, DatabaseBugreportObject.PREVIOUS_SERVER_ID).getAsString());
                    pp.sendMessage("§8\u00BB §7Extra Nachricht: §e" + databaseManager.getDatabaseBugreport().getDatabaseElement(bugId, DatabaseBugreportObject.EXTRA_MESSAGE).getAsString());
                    pp.sendMessage("§8\u00BB §7Bug-Status: " + BugState.valueOf(databaseManager.getDatabaseBugreport().getDatabaseElement(bugId, DatabaseBugreportObject.STATE).getAsString()).getBetterString());
                    pp.sendMessage("§8\u00BB §7Erstellt am: §e" + databaseManager.getDatabaseBugreport().getDatabaseElement(bugId, DatabaseBugreportObject.CREATE_DATE).getAsString());
                    if (hasRank) {
                        pp.sendMessage("§8\u00BB §7Erstellt von: §9" + gameChest.getProxy().getPlayer(UUID.fromString(databaseManager.getDatabaseBugreport().getDatabaseElement(bugId, DatabaseBugreportObject.CREATED_BY).getAsString())));
                    }
                    return;
                }
            }

            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("edit")) {
                    if (!gameChest.equalsRank(pp.getUniqueId(), Rank.DEVELOPER)) {
                        sender.sendMessage(ChestPrefix.PREFIX + "§cDu hast keine Berechtigung für diesen Befehl!");
                        return;
                    }
                    String bugId = args[1];
                    if (!databaseManager.getDatabaseBugreport().existsBugreport(bugId)) {
                        sender.sendMessage(ChestPrefix.PREFIX + "§cDieser Bug-Report existiert nicht!");
                        return;
                    }

                    String stateStr = args[2];

                    if (!BugState.getBugStateAsString().contains(stateStr)) {
                        sender.sendMessage(ChestPrefix.PREFIX + "§cDieser Bug-Status existiert nicht!");
                        sender.sendMessage("§7Reasons: " + BugState.getBugStateAsString().toString());
                        return;
                    }

                    BugState bugState = BugState.getBugState(stateStr);

                    databaseManager.getDatabaseBugreport().setDatabaseObject(bugId, DatabaseBugreportObject.STATE, bugState.toString());
                    sender.sendMessage(ChestPrefix.PREFIX + "§7Bug-Report bearbeitet: " + bugState.getBetterString());
                    return;
                }
            }

            if (args.length > 1) {
                String reasonStr = args[0];

                if (!BugReason.getBetterBugReasonsAsString().contains(reasonStr)) {
                    StringBuilder reasons = new StringBuilder();
                    for (String rs : BugReason.getBetterBugReasonsAsString()) {
                        reasons.append("§e").append(rs).append("§7, ");
                    }
                    reasons.append("#");
                    reasons = new StringBuilder(reasons.toString().replace(", #", ""));

                    sender.sendMessage(ChestPrefix.PREFIX + "§cKein Bug-Grund!");
                    sender.sendMessage("§8\u00BB §7Bug-Gründe: " + reasons);
                    return;
                }
                BugReason bugReason = BugReason.getBugReason(reasonStr);
                databaseManager.getAsync().getOnlinePlayer(pp.getUniqueId(), dbOPlayer-> {
                    StringBuilder extra = new StringBuilder();

                    for (int i = 1; i < args.length; i++) {
                        extra.append(args[i]).append(" ");
                    }
                    extra.append("#");
                    extra = new StringBuilder(extra.toString().replace(" #", ""));

                    String bugId = "#BR" + (databaseManager.getDatabaseBugreport().getReportedBugs() + 2);
                    String serverId = pp.getServer().getInfo().getName();
                    String previousServerId = null;
                    if (dbOPlayer.getDatabaseElement(DatabaseOnlinePlayerObject.PREVIOUS_SERVER_ID).getObject() != null)
                        previousServerId = dbOPlayer.getDatabaseElement(DatabaseOnlinePlayerObject.PREVIOUS_SERVER_ID).getAsString();

                    databaseManager.getDatabaseBugreport().createBugreport(bugId, bugReason, serverId, extra.toString(), pp.getUniqueId(), previousServerId);
                    sender.sendMessage(ChestPrefix.PREFIX_BUG_REPORT + "§aDein Bug-Report wurde erfolgreich erstellt!");
                    sender.sendMessage("§8\u00BB §7BugID: §e" + bugId);
                    sender.sendMessage("§8\u00BB §7Grund: §a" + bugReason.getBetterReason());
                    sender.sendMessage("§8\u00BB §7Deine Nachricht: §7" + extra);
                    sender.sendMessage("§8\u00BB §6Den Status kannst du unter §c/buginfo <BugId>§6 einsehen.");
                    for (ProxiedPlayer player : gameChest.getProxy().getPlayers()) {
                        if (gameChest.hasRank(player.getUniqueId(), Rank.DEVELOPER)) {
                            player.sendMessage(ChestPrefix.PREFIX_BUG_REPORT + "§a" + sender.getName() + "§b hat einen Bug reportet! §7(§c" + bugId + "§7)");
                        }
                    }
                }, DatabaseOnlinePlayerObject.PREVIOUS_SERVER_ID);

                return;
            }
            sender.sendMessage(ChestPrefix.PREFIX + "§7Bitte nutze diesen Befehl nur, um einen Bug zu reporten!");
            sender.sendMessage(ChestPrefix.PREFIX + "§c/bugreport <Grund> <Eigene Nachricht>");
            if (gameChest.hasRank(pp.getUniqueId(), Rank.DEVELOPER)) {
                sender.sendMessage(ChestPrefix.PREFIX + "§c/bugreport info <Bug-ID>");
                sender.sendMessage(ChestPrefix.PREFIX + "§c/bugreport edit <Bug-ID> <State>");
                sender.sendMessage(ChestPrefix.PREFIX + "§c/bugreport waiting");
                sender.sendMessage(ChestPrefix.PREFIX + "§c/bugreport all");
            }
            return;
        }
        sender.sendMessage("§cNur als Player nutzbar!");
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length > 1 || args.length == 0) {
            return ImmutableSet.of();
        }

        Set<String> matches = new HashSet<>();
        String search = args[0].toLowerCase();
        for (String bugReason : BugReason.getBetterBugReasonsAsString()) {
            if(bugReason.toLowerCase().startsWith(search)) {
                matches.add(bugReason);
            }
        }
        if ("info".startsWith(search)) {
            matches.add("info");
        }

        return matches;
    }
}
