package de.gamechest.commands.report.bug;

import de.gamechest.GameChest;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.common.ChestPrefix;
import de.gamechest.database.DatabaseManager;
import de.gamechest.database.bug.BugReason;
import de.gamechest.database.bug.BugState;
import de.gamechest.database.bug.DatabaseBugreportObject;
import de.gamechest.common.Rank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/**
 * Created by ByteList on 26.04.2017.
 */
public class BugInfoCommand extends GCCommand {

    public BugInfoCommand() {
        super("buginfo");
    }

    private GameChest gameChest = GameChest.getInstance();
    private DatabaseManager databaseManager = gameChest.getDatabaseManager();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            if(args.length == 1) {
                String bugId = args[0];

                if(!databaseManager.getDatabaseBugreport().existsBugreport(bugId)) {
                    sender.sendMessage(ChestPrefix.PREFIX+"§cDieser Bug-Report existiert nicht!");
                    return;
                }

                boolean hasRank = true;

                if(!gameChest.hasRank(pp.getUniqueId(), Rank.DEVELOPER)) {
                    hasRank = false;
                    if(!pp.getUniqueId().toString().equals(databaseManager.getDatabaseBugreport().getDatabaseElement(bugId, DatabaseBugreportObject.CREATED_BY).getAsString())) {
                        sender.sendMessage(ChestPrefix.PREFIX+"§cDieser Bug-Report wurde nicht von dir erstellt!");
                        return;
                    }
                }

                pp.sendMessage(ChestPrefix.PREFIX_BUG_REPORT +"§bInfo über den Bug-Report §e"+bugId+"§b:");
                pp.sendMessage("§8\u00BB §7Grund: §a"+ BugReason.valueOf(databaseManager.getDatabaseBugreport().getDatabaseElement(bugId, DatabaseBugreportObject.REASON).getAsString()).getBetterReason());
                pp.sendMessage("§8\u00BB §7Server-ID: §a"+databaseManager.getDatabaseBugreport().getDatabaseElement(bugId, DatabaseBugreportObject.SERVER_ID).getAsString());
                if(databaseManager.getDatabaseBugreport().getDatabaseElement(bugId, DatabaseBugreportObject.PREVIOUS_SERVER_ID).getObject() != null)
                    pp.sendMessage("§8\u00BB §7Vorherige Server-ID: §e"+ databaseManager.getDatabaseBugreport().getDatabaseElement(bugId, DatabaseBugreportObject.PREVIOUS_SERVER_ID).getAsString());
                pp.sendMessage("§8\u00BB §7Extra Nachricht: §e"+databaseManager.getDatabaseBugreport().getDatabaseElement(bugId, DatabaseBugreportObject.EXTRA_MESSAGE).getAsString());
                pp.sendMessage("§8\u00BB §7Report-Status: "+ BugState.valueOf(databaseManager.getDatabaseBugreport().getDatabaseElement(bugId, DatabaseBugreportObject.STATE).getAsString()).getBetterString());
                pp.sendMessage("§8\u00BB §7Erstellt am: §e"+ databaseManager.getDatabaseBugreport().getDatabaseElement(bugId, DatabaseBugreportObject.CREATE_DATE).getAsString());
                if(hasRank) {
                    pp.sendMessage("§8\u00BB §7Erstellt von: §9"+ gameChest.getProxy().getPlayer(UUID.fromString(databaseManager.getDatabaseBugreport().getDatabaseElement(bugId, DatabaseBugreportObject.CREATED_BY).getAsString())));
                }
                return;
            }
            sender.sendMessage(ChestPrefix.PREFIX+"§c/buginfo <Bug-ID>");
        } else
            sender.sendMessage("§cNur für Player!");
    }
}
