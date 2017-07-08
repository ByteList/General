package de.gamechest.commands;

import de.gamechest.ConnectManager;
import de.gamechest.GameChest;
import de.gamechest.UUIDFetcher;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.database.rank.Rank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/**
 * Created by ByteList on 12.02.2017.
 */
public class GcgCommand extends GCCommand {

    public GcgCommand() {
        super("gcg");
    }

    private GameChest gameChest = GameChest.getInstance();
    private ConnectManager connectManager = gameChest.getConnectManager();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            if(!gameChest.hasRank(pp.getUniqueId(), Rank.DEVELOPER)) {
                sender.sendMessage(gameChest.prefix+"§cDu hast keine Berechtigung für diesen Befehl!");
                return;
            }
        }

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("mode")) {
                String value = args[1];
                
                if(value.equalsIgnoreCase(connectManager.getConnectState().toString())) {
                    sender.sendMessage(gameChest.prefix+"§cDieser ConnectState ist bereits eingestellt!");
                    return;
                }

                if(value.toUpperCase().equalsIgnoreCase("open")) {
                    connectManager.setConnectState(ConnectManager.ConnectState.OPEN);
                    sender.sendMessage(gameChest.prefix+"§7ConnectState auf §aOPEN §7geändert!");
                    return;
                }

                if(value.toUpperCase().equalsIgnoreCase("whitelist")) {
                    connectManager.setConnectState(ConnectManager.ConnectState.WHITELIST);
                    sender.sendMessage(gameChest.prefix+"§7ConnectState auf §eWHITELIST §7geändert!");
                    return;
                }

                if(value.toUpperCase().equalsIgnoreCase("maintenance")) {
                    connectManager.setConnectState(ConnectManager.ConnectState.MAINTENANCE);
                    sender.sendMessage(gameChest.prefix+"§7ConnectState auf §cMAINTENANCE §7geändert!");
                    return;
                }

                if(value.toUpperCase().equalsIgnoreCase("development")) {
                    connectManager.setConnectState(ConnectManager.ConnectState.DEVELOPMENT);
                    sender.sendMessage(gameChest.prefix+"§7ConnectState auf §bDEVELOPMENT §7geändert!");
                    return;
                }
                sender.sendMessage("§8\u00BB §c/gcg mode [open/whitelist/maintenance/development]");
                return;
            }

            if(args[0].equalsIgnoreCase("plimit")) {
                Integer plimit;
                try {
                    plimit = Integer.parseInt(args[1]);
                } catch (NumberFormatException ex) {
                    sender.sendMessage(gameChest.prefix+"§c<Limit> = Zahl");
                    return;
                }
                connectManager.setPlayerLimit(plimit);
                sender.sendMessage(gameChest.prefix+"§7Playerlimit auf §e"+plimit+"§7 geändert!");
                return;
            }
        }

        if(args.length == 3) {
            if(args[0].equalsIgnoreCase("whitelist")) {
                if(args[1].equalsIgnoreCase("add")) {
                    String name = args[2];
                    UUID uuid = UUIDFetcher.getUUID(name);

                    if(connectManager.getWhiteList().contains(uuid)) {
                        sender.sendMessage(gameChest.prefix+"§c"+name+" steht schon auf der WhiteList");
                        return;
                    }

                    connectManager.addUuidToWhiteList(uuid);
                    sender.sendMessage(gameChest.prefix+"§e"+name+" wurde zur WhiteList hinzugefügt");
                    return;
                }
                if(args[1].equalsIgnoreCase("remove")) {
                    String name = args[2];
                    UUID uuid = UUIDFetcher.getUUID(name);

                    if(!connectManager.getWhiteList().contains(uuid)) {
                        sender.sendMessage(gameChest.prefix+"§c"+name+" steht nicht auf der WhiteList");
                        return;
                    }

                    connectManager.removeUuidFromWhiteList(uuid);
                    sender.sendMessage(gameChest.prefix+"§e"+name+" wurde von der WhiteList entfernt");
                    return;
                }
            }
        }

        if(args.length > 2) {
            if(args[0].equalsIgnoreCase("motd")) {
                String motd = "";
                for (int i = 1; i < args.length; i++) {
                    motd = motd + args[i] + " ";
                }
                connectManager.setMotd(motd);
                sender.sendMessage(gameChest.prefix+"§eMotd erfolgreich geändert:");
                sender.sendMessage(motd.replace("&", "§"));
                return;
            }
        }

        sender.sendMessage(gameChest.prefix+"§7Alle GCG-Befehle:");
        sender.sendMessage("§8\u00BB §c/gcg mode [open/whitelist/maintenance/development]");
        sender.sendMessage("§8\u00BB §c/gcg motd [Nachricht] - Farbcodes über: &");
        sender.sendMessage("§8\u00BB §c/gcg plimit <Limit>");
        sender.sendMessage("§8\u00BB §c/gcg whitelist add <Player>");
        sender.sendMessage("§8\u00BB §c/gcg whitelist remove <Player>");
    }
}
