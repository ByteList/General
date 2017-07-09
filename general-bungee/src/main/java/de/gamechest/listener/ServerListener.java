package de.gamechest.listener;

import de.bytelist.bytecloud.bungee.ByteCloudMaster;
import de.gamechest.GameChest;
import de.gamechest.party.Party;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by ByteList on 27.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ServerListener implements Listener {

    private final GameChest gameChest = GameChest.getInstance();

    @EventHandler
    public void onServerKick(ServerKickEvent e) {
        if(gameChest.isCloudEnabled()) {
            try {
                System.out.println(e.getKickedFrom().getName());
                System.out.println(e.getPlayer().getServer().getInfo().getName());
                String randomLobbyId = ByteCloudMaster.getInstance().getCloudHandler().getRandomLobbyId(e.getKickedFrom().getName());
                System.out.println(randomLobbyId);
                e.setCancelled(true);
                ServerInfo serverInfo = gameChest.getProxy().getServerInfo(randomLobbyId);
                e.setCancelServer(serverInfo);
                e.getPlayer().sendMessage("§7Du wurdest vom Server gekickt:§r "+e.getKickReason());
            } catch (Exception ignored) {
                e.setCancelled(false);
                e.setKickReason(ByteCloudMaster.getInstance().prefix+"§cDer Cloud-Server konnte nicht erreicht werden.");
            }
        }
    }

    @EventHandler
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
    }
}
