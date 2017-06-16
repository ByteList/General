package de.gamechest.commands.cloud;

import de.bytelist.bytecloud.bungee.ByteCloudMaster;
import de.bytelist.bytecloud.database.DatabaseServerObject;
import de.bytelist.bytecloud.network.NetworkManager;
import de.bytelist.bytecloud.network.bungee.packet.PacketInStartServer;
import de.bytelist.bytecloud.network.bungee.packet.PacketInStopServer;
import de.gamechest.GameChest;
import de.gamechest.UUIDFetcher;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.database.DatabasePlayer;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.rank.Rank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/**
 * Created by ByteList on 29.01.2017.
 */
public class ServerCommand extends GCCommand {

    public ServerCommand() {
        super("server", "s");
    }

    private GameChest gameChest = GameChest.getInstance();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (gameChest.getProxy().getPluginManager().getPlugin("ByteCloud-Master") == null) return;
        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            if(!gameChest.hasRank(pp.getUniqueId(), Rank.SUPPORTER)) {
                sender.sendMessage(ByteCloudMaster.getInstance().prefix+"§cDu hast keine Berechtigung für diesen Befehl!");
                return;
            }
        }

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("list")) {
                sender.sendMessage(ByteCloudMaster.getInstance().prefix+"§7Server-List:");
                for(String serverId : ByteCloudMaster.getInstance().getCloudHandler().getServerInDatabase()) {
                    int port = ByteCloudMaster.getInstance().getCloudHandler().getDatabaseServer().getDatabaseElement(serverId, DatabaseServerObject.PORT).getAsInt();
                    if(sender instanceof ProxiedPlayer) {
                        ProxiedPlayer pp = (ProxiedPlayer) sender;
                        if(!gameChest.hasRank(pp.getUniqueId(), Rank.DEVELOPER)) {
                            sender.sendMessage("§8\u00BB §e"+serverId);
                        } else
                            sender.sendMessage("§8\u00BB §e"+serverId+ "§8 - §aPort: §c"+port);
                    } else
                        sender.sendMessage("§8\u00BB §e"+serverId+ "§8 - §aPort: §c"+port);
                }
                return;
            }
        }

        if(args.length == 2) {
            if(sender instanceof ProxiedPlayer) {
                ProxiedPlayer pp = (ProxiedPlayer) sender;
                if(!gameChest.hasRank(pp.getUniqueId(), Rank.DEVELOPER)) {
                    sender.sendMessage(ByteCloudMaster.getInstance().prefix+"§cDu hast keine Berechtigung für diesen Befehl!");
                    return;
                }
            }
            if(args[0].equalsIgnoreCase("start")) {
                String group = args[1].toUpperCase();
                PacketInStartServer packetInStartServer = new PacketInStartServer(group, sender.getName());
                NetworkManager.getBungeeClient().sendPacket(packetInStartServer);
                return;
            }
            if(args[0].equalsIgnoreCase("stop")) {
                String serverId = ByteCloudMaster.getInstance().getCloudHandler().getUniqueServerId(args[1]);

                PacketInStopServer packetInStopServer = new PacketInStopServer(serverId, sender.getName());
                NetworkManager.getBungeeClient().sendPacket(packetInStopServer);
                return;
            }

            if(args[0].equalsIgnoreCase("info")) {
                String serverId = ByteCloudMaster.getInstance().getCloudHandler().getUniqueServerId(args[1]);
                
                if(!ByteCloudMaster.getInstance().getCloudHandler().getDatabaseServer().existsServer(serverId)) {
                    sender.sendMessage(ByteCloudMaster.getInstance().prefix+"§cKonnte keinen passenden Server finden!");
                    return;
                }

                String listP = "";
                String connectedPlayer = ByteCloudMaster.getInstance().getCloudHandler().getDatabaseServer().getDatabaseElement(serverId, DatabaseServerObject.PLAYERS).getAsString();

                if(connectedPlayer.contains(",")) {
                    String[] cpS = connectedPlayer.split(",");
                    for (String player : cpS) {
                        UUID uuid = UUIDFetcher.getUUID(player);
                        DatabasePlayer databasePlayer = gameChest.getDatabaseManager().getDatabasePlayer(uuid);
                        String ppColor = Rank.getRankById(databasePlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt()).getColor();
                        listP = listP + ppColor + player + "\n";
                    }
                }

                TextComponent listPlayer = new TextComponent("§8\u00BB §f§o[Spieler anzeigen]");
                listPlayer.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(listP).create()));

                String listS = "";
                String connectedSpectator = ByteCloudMaster.getInstance().getCloudHandler().getDatabaseServer().getDatabaseElement(serverId, DatabaseServerObject.SPECTATORS).getAsString();

                if(connectedSpectator.contains(",")) {
                    String[] csS = connectedSpectator.split(",");
                    for (String player : csS) {
                        UUID uuid = UUIDFetcher.getUUID(player);
                        DatabasePlayer databasePlayer = gameChest.getDatabaseManager().getDatabasePlayer(uuid);
                        String ppColor = Rank.getRankById(databasePlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt()).getColor();
                        listS = listS + ppColor + player + "\n";
                    }
                }

                TextComponent listSpectator = new TextComponent("§8\u00BB §f§o[Zuschauer anzeigen]");
                listSpectator.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(listS).create()));

                String group = ByteCloudMaster.getInstance().getCloudHandler().getDatabaseServer().getDatabaseElement(serverId, DatabaseServerObject.GROUP).getAsString();
                String serverState = ByteCloudMaster.getInstance().getCloudHandler().getDatabaseServer().getDatabaseElement(serverId, DatabaseServerObject.STATE).getAsString();
                String motd = ByteCloudMaster.getInstance().getCloudHandler().getDatabaseServer().getDatabaseElement(serverId, DatabaseServerObject.MOTD).getAsString();
                Integer onlinePlayer = ByteCloudMaster.getInstance().getCloudHandler().getDatabaseServer().getDatabaseElement(serverId, DatabaseServerObject.PLAYER_ONLINE).getAsInt();
                Integer maxPlayer = ByteCloudMaster.getInstance().getCloudHandler().getDatabaseServer().getDatabaseElement(serverId, DatabaseServerObject.PLAYER_MAX).getAsInt();
                Integer onlineSpectator = ByteCloudMaster.getInstance().getCloudHandler().getDatabaseServer().getDatabaseElement(serverId, DatabaseServerObject.SPECTATOR_ONLINE).getAsInt();
                Integer maxSpectator = ByteCloudMaster.getInstance().getCloudHandler().getDatabaseServer().getDatabaseElement(serverId, DatabaseServerObject.SPECTATOR_MAX).getAsInt();

                sender.sendMessage("");
                sender.sendMessage(ByteCloudMaster.getInstance().prefix+"§6Serverinformationen §7(§e"+serverId+"§7)§6:");
                sender.sendMessage("§8\u00BB §7Gruppe: §6"+group);
                sender.sendMessage("§8\u00BB §7Join-Status: §a"+serverState);
                sender.sendMessage("§8\u00BB §7Motd: §e"+motd);
                sender.sendMessage("§8\u00BB §7Spieler: §a"+onlinePlayer+"§7/§c"+maxPlayer);
                sender.sendMessage(listPlayer);
                sender.sendMessage("§8\u00BB §7Zuschauer: §b"+onlineSpectator+"§7/§9"+maxSpectator);
                sender.sendMessage(listSpectator);
                return;
            }
        }

        sender.sendMessage(ByteCloudMaster.getInstance().prefix+"§7Alle Server-Befehle:");
        sender.sendMessage("§8\u00BB §c/server start <group>");
        sender.sendMessage("§8\u00BB §c/server stop <id>");
        sender.sendMessage("§8\u00BB §c/server info <id>");
        sender.sendMessage("§8\u00BB §c/server list");
    }
}
