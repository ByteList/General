package de.gamechest.listener;

import de.gamechest.GameChest;
import de.gamechest.database.DatabaseManager;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.UUID;

/**
 * Created by ByteList on 10.04.2017.
 */
public class PlayerDisconnectListener implements Listener {

    private final DatabaseManager databaseManager = GameChest.getInstance().getDatabaseManager();
    private final GameChest gameChest = GameChest.getInstance();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDisconnect(PlayerDisconnectEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();

        gameChest.rankCache.remove(uuid);

        gameChest.getDatabaseManager().getAsync().getPlayer(uuid, dbPlayer -> {
            if(dbPlayer.existsPlayer()) {
                gameChest.getNick().unnickOnDisconnect(e.getPlayer());

                gameChest.TELL_FROM_TO.remove(e.getPlayer());
                gameChest.onlineTeam.remove(e.getPlayer());

                if(gameChest.getPartyManager().isPlayerInAParty(uuid))
                    gameChest.getPartyManager().leaveParty(gameChest.getPartyManager().getParty(uuid).getPartyId(), e.getPlayer());

                databaseManager.getAsync().getOnlinePlayer(uuid, DatabaseOnlinePlayer::removeOnlinePlayer);
            }
        });
    }
}
