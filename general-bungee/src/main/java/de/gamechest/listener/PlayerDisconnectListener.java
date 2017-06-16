package de.gamechest.listener;

import de.gamechest.GameChest;
import de.gamechest.database.DatabaseManager;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

/**
 * Created by ByteList on 10.04.2017.
 */
public class PlayerDisconnectListener implements Listener {

    private DatabaseManager databaseManager = GameChest.getInstance().getDatabaseManager();

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();

        GameChest.getInstance().getNick().unnickOnDisconnect(e.getPlayer());

        databaseManager.getDatabaseOnlinePlayer(uuid).removeOnlinePlayer();
        databaseManager.removeCachedDatabaseOnlinePlayer(uuid);
        databaseManager.removeCachedDatabasePlayer(uuid);

        if(GameChest.getInstance().TELL_FROM_TO.containsKey(e.getPlayer()))
            GameChest.getInstance().TELL_FROM_TO.remove(e.getPlayer());
    }
}
