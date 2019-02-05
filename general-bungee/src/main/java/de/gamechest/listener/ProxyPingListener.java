package de.gamechest.listener;

import de.gamechest.ConnectManager;
import de.gamechest.GameChest;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 * Created by ByteList on 12.02.2017.
 *
 * Copyright by ByteList - https://bytelist.de/
 */
public class ProxyPingListener implements Listener {

    private GameChest gameChest = GameChest.getInstance();
    private ConnectManager connectManager = gameChest.getConnectManager();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPing(ProxyPingEvent e) {
        PendingConnection pc = e.getConnection();
        ServerPing serverPing = e.getResponse();
        ServerPing.Players players = serverPing.getPlayers();
        ServerPing.Protocol protocol = serverPing.getVersion();

        ConnectManager.ConnectState connectState = connectManager.getConnectState();

        String firstLine = "§6Game-ChestPrefix§f.§6de §7\u00BB §eSurvival §f& §eSpielmodi §8[§b1.9 §f- §c1.12§8]\n";

        /*
         * 315 - 1.11 | 210 - 1.10 | 107 - 1.9
         */

        if(pc.getVersion() < 100) {
            protocol.setName("§4ChestBungee");
            protocol.setProtocol(2);
            serverPing.setVersion(protocol);
            serverPing.setDescription(firstLine+"§cBitte verwende eine Minecraft-Version ab 1.9!");
            return;
        }

        if(connectState == ConnectManager.ConnectState.DEVELOPMENT) {
            protocol.setName("§r");
            protocol.setProtocol(2);
            serverPing.setVersion(protocol);
            serverPing.setDescription(firstLine+connectManager.getMotd("development").replace("&", "§"));
            return;
        }

        if(connectState == ConnectManager.ConnectState.MAINTENANCE) {
            protocol.setName("§r");
            protocol.setProtocol(2);
            serverPing.setVersion(protocol);
            serverPing.setDescription(firstLine+connectManager.getMotd("maintenance").replace("&", "§"));
            return;
        }

        if(connectState == ConnectManager.ConnectState.WHITELIST) {
            protocol.setName("§r");
            protocol.setProtocol(2);
            serverPing.setVersion(protocol);
            serverPing.setDescription(firstLine+connectManager.getMotd("whitelist").replace("&", "§"));
            return;
        }

        if(connectState == ConnectManager.ConnectState.OPEN) {
            players.setMax(connectManager.getPlayerLimit());
            protocol.setName(protocol.getName());
            protocol.setProtocol(protocol.getProtocol());
            serverPing.setPlayers(players);
            serverPing.setVersion(protocol);
            serverPing.setDescription(firstLine+connectManager.getMotd("open").replace("&", "§"));
        }
    }
}
