package de.gamechest.listener;

import de.bytelist.bytecloud.bungee.ByteCloudMaster;
import de.gamechest.GameChest;
import de.gamechest.database.DatabaseElement;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayerObject;
import de.gamechest.database.terms.DatabaseTermsObject;
import de.gamechest.party.Party;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 * Created by ByteList on 27.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ServerListener implements Listener {

    private final GameChest gameChest = GameChest.getInstance();
    private final ByteCloudMaster byteCloudMaster = ByteCloudMaster.getInstance();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerKick(ServerKickEvent e) {
        ProxiedPlayer player = e.getPlayer();

        if(gameChest.isCloudEnabled()) {
            try {
                if(byteCloudMaster.getForcedJoinServerId().equals(e.getKickedFrom().getName())) {
                    e.setCancelled(false);
                    e.setKickReason("§7Du wurdest vom Server gekickt:§r "+e.getKickReason());
                    return;
                }
                e.setCancelled(true);
                String randomLobbyId = byteCloudMaster.getCloudHandler().getRandomLobbyId(e.getKickedFrom().getName());
                ServerInfo serverInfo = gameChest.getProxy().getServerInfo(randomLobbyId);
                e.setCancelServer(serverInfo);
                player.sendMessage("§7Du wurdest vom Server gekickt:§r "+e.getKickReason());
            } catch (Exception ignored) {
                e.setCancelled(false);
                e.setKickReason("§7Du wurdest vom Server gekickt:§r "+e.getKickReason());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerSwitch(ServerSwitchEvent e) {
        ProxiedPlayer player = e.getPlayer();

        if(gameChest.getDatabaseManager().getDatabaseTerms().existsPlayer(player.getUniqueId()) &&
                gameChest.getDatabaseManager().getDatabaseTerms().getDatabaseElement(player.getUniqueId(), DatabaseTermsObject.STATE).getAsInt() == 0) {
            return;
        }

        if(gameChest.getPartyManager().isPlayerInAParty(player.getUniqueId())) {
            Party party = gameChest.getPartyManager().getParty(player.getUniqueId());

            if(!party.getLeader().getUniqueId().equals(player.getUniqueId())) {
                return;
            }
            if(player.getServer().getInfo().getName().startsWith("lb-")) {
                return;
            }

            for(ProxiedPlayer p : party.getMember()) {
                p.connect(player.getServer().getInfo());
            }
        }

        gameChest.getDatabaseManager().getAsync().getOnlinePlayer(player.getUniqueId(), databaseOnlinePlayer -> {
            DatabaseElement databaseElement = databaseOnlinePlayer.getDatabaseElement(DatabaseOnlinePlayerObject.SERVER_ID);
            if(databaseElement.getObject() == null) {
                databaseOnlinePlayer.setDatabaseObject(DatabaseOnlinePlayerObject.SERVER_ID, player.getServer().getInfo().getName());
            } else {
                databaseOnlinePlayer.setDatabaseObject(DatabaseOnlinePlayerObject.PREVIOUS_SERVER_ID, databaseElement.getAsString());
                databaseOnlinePlayer.setDatabaseObject(DatabaseOnlinePlayerObject.SERVER_ID, player.getServer().getInfo().getName());
            }
        }, DatabaseOnlinePlayerObject.SERVER_ID, DatabaseOnlinePlayerObject.PREVIOUS_SERVER_ID);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerConnect(ServerConnectEvent e) {
        ProxiedPlayer player = e.getPlayer();

        if(gameChest.getDatabaseManager().getDatabaseTerms().existsPlayer(player.getUniqueId()) &&
                gameChest.getDatabaseManager().getDatabaseTerms().getDatabaseElement(player.getUniqueId(), DatabaseTermsObject.STATE).getAsInt() == 0) {
            e.setTarget(ProxyServer.getInstance().getServerInfo("PreLogin"));
        }
    }
}
