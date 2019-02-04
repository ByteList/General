package de.gamechest.commands.cloud;

import de.bytelist.bytecloud.bungee.ByteCloudMaster;
import de.bytelist.bytecloud.database.DatabaseServer;
import de.bytelist.bytecloud.database.DatabaseServerObject;
import de.bytelist.bytecloud.network.bungee.PacketInStartServer;
import de.bytelist.bytecloud.network.bungee.PacketInStopServer;
import de.gamechest.GameChest;
import de.gamechest.UUIDFetcher;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.common.Rank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/**
 * Created by ByteList on 29.01.2017.
 *
 * Copyright by ByteList - https://bytelist.de/
 */
public class ServerCommand extends GCCommand {

    public ServerCommand() {
        super("server", "s");
    }

    private final GameChest gameChest = GameChest.getInstance();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!gameChest.isCloudEnabled()) return;
        ByteCloudMaster byteCloudMaster = ByteCloudMaster.getInstance();
        
        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            if(!gameChest.hasRank(pp.getUniqueId(), Rank.SUPPORTER)) {
                gameChest.sendNoPermissionMessage(sender);
                return;
            }
        }

        DatabaseServer databaseServer = byteCloudMaster.getCloudHandler().getDatabaseServer();

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("list")) {
                sender.sendMessage(byteCloudMaster.prefix+"§7Server-List:");
                for(String serverId : byteCloudMaster.getCloudHandler().getServerInDatabase()) {
                    int port = databaseServer.getDatabaseElement(serverId, DatabaseServerObject.PORT).getAsInt();
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
                    gameChest.sendNoPermissionMessage(sender);
                    return;
                }
            }
            if(args[0].equalsIgnoreCase("start")) {
                String group = args[1].toUpperCase();
                PacketInStartServer packetInStartServer = new PacketInStartServer(group, sender.getName());
                byteCloudMaster.getBungeeClient().sendPacket(packetInStartServer);
                return;
            }
            if(args[0].equalsIgnoreCase("stop")) {
                String serverId = byteCloudMaster.getCloudHandler().getUniqueServerId(args[1]);

                ServerInfo currentServer = gameChest.getProxy().getServerInfo(serverId);

                new Thread("Cloud-Stop-"+currentServer.getName()) {
                    @Override
                    public void run() {
//                        while (true) {
//                            if(currentServer.getPlayers().size() == 0) break;
//                        }
                        try {
                            Thread.sleep(2000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        PacketInStopServer packetInStopServer = new PacketInStopServer(currentServer.getName(), sender.getName());
                        byteCloudMaster.getBungeeClient().sendPacket(packetInStopServer);
                    }
                }.start();
                return;
            }

            if(args[0].equalsIgnoreCase("info")) {
                String serverId = byteCloudMaster.getCloudHandler().getUniqueServerId(args[1]);
                
                if(!databaseServer.existsServer(serverId)) {
                    sender.sendMessage(byteCloudMaster.prefix+"§cKonnte keinen passenden Server finden!");
                    return;
                }

                StringBuilder listP = new StringBuilder();
                String connectedPlayer = databaseServer.getDatabaseElement(serverId, DatabaseServerObject.PLAYERS).getAsString();

                if(connectedPlayer.contains(",")) {
                    String[] cpS = connectedPlayer.split(",");
                    for (String player : cpS) {
                        UUID uuid = UUIDFetcher.getUUID(player);
                        String ppColor = gameChest.getRank(uuid).getColor();
                        listP.append(ppColor).append(player).append("\n");
                    }
                }



                StringBuilder listS = new StringBuilder();
                String connectedSpectator = databaseServer.getDatabaseElement(serverId, DatabaseServerObject.SPECTATORS).getAsString();

                if(connectedSpectator.contains(",")) {
                    String[] csS = connectedSpectator.split(",");
                    for (String player : csS) {
                        UUID uuid = UUIDFetcher.getUUID(player);
                        String ppColor = gameChest.getRank(uuid).getColor();
                        listS.append(ppColor).append(player).append("\n");
                    }
                }

                String group = databaseServer.getDatabaseElement(serverId, DatabaseServerObject.GROUP).getAsString();
                String serverStart = databaseServer.getDatabaseElement(serverId, DatabaseServerObject.STARTED).getAsString();
                String serverState = databaseServer.getDatabaseElement(serverId, DatabaseServerObject.STATE).getAsString();
                String motd = databaseServer.getDatabaseElement(serverId, DatabaseServerObject.MOTD).getAsString();
                Integer onlinePlayer = databaseServer.getDatabaseElement(serverId, DatabaseServerObject.PLAYER_ONLINE).getAsInt();
                Integer maxPlayer = databaseServer.getDatabaseElement(serverId, DatabaseServerObject.PLAYER_MAX).getAsInt();
                Integer onlineSpectator = databaseServer.getDatabaseElement(serverId, DatabaseServerObject.SPECTATOR_ONLINE).getAsInt();
                Integer maxSpectator = databaseServer.getDatabaseElement(serverId, DatabaseServerObject.SPECTATOR_MAX).getAsInt();

                TextComponent listPlayer = new TextComponent("§8\u00BB §7Spieler: §a"+onlinePlayer+"§7/§c"+maxPlayer);
                listPlayer.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(listP.toString()).create()));

                TextComponent listSpectator = new TextComponent("§8\u00BB §7Zuschauer: §b"+onlineSpectator+"§7/§9"+maxSpectator);
                listSpectator.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(listS.toString()).create()));

                sender.sendMessage("");
                sender.sendMessage(byteCloudMaster.prefix+"§6Serverinformationen §7(§e"+serverId+"§7)§6:");
                sender.sendMessage("§8\u00BB §7Gruppe: §6"+group);
                sender.sendMessage("§8\u00BB §7Server-Start: §e"+serverStart);
                sender.sendMessage("§8\u00BB §7Join-Status: §a"+serverState);
                sender.sendMessage("§8\u00BB §7Motd: §e"+motd);
                sender.sendMessage(listPlayer);
                sender.sendMessage(listSpectator);
                return;
            }
        }

        sender.sendMessage(byteCloudMaster.prefix+"§7Alle Server-Befehle:");
        sender.sendMessage("§8\u00BB §c/server start <group>");
        sender.sendMessage("§8\u00BB §c/server stop <id>");
        sender.sendMessage("§8\u00BB §c/server info <id>");
        sender.sendMessage("§8\u00BB §c/server list");
    }
}
