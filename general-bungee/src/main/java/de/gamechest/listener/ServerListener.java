package de.gamechest.listener;

import de.bytelist.bytecloud.bungee.ByteCloudMaster;
import de.gamechest.GameChest;
import de.gamechest.database.DatabaseElement;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayerObject;
import de.gamechest.party.Party;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerKick(ServerKickEvent e) {
        if(gameChest.isCloudEnabled()) {
            try {
                String randomLobbyId = ByteCloudMaster.getInstance().getCloudHandler().getRandomLobbyId(e.getKickedFrom().getName());
                e.setCancelled(true);
                ServerInfo serverInfo = gameChest.getProxy().getServerInfo(randomLobbyId);
                e.setCancelServer(serverInfo);
                e.getPlayer().sendMessage("§7Du wurdest vom Server gekickt:§r "+e.getKickReason());
            } catch (Exception ignored) {
                e.setCancelled(false);
                e.setKickReason(ByteCloudMaster.getInstance().prefix+"§cDer Cloud-Server hat deine Verbindung getrennt.");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerSwitch(ServerSwitchEvent e) {
        ProxiedPlayer player = e.getPlayer();

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
}
