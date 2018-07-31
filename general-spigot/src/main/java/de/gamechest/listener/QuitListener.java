package de.gamechest.listener;

import de.gamechest.GameChest;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by ByteList on 10.04.2017.
 */
public class QuitListener implements Listener {

    private final GameChest gameChest = GameChest.getInstance();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        gameChest.rankCache.remove(player.getUniqueId());

        if(Bukkit.getServerName().contains("nonBungee")) {
            delete(e.getPlayer());
        }

        gameChest.getNick().removeFromCache(player.getUniqueId());
        gameChest.getPacketInjector().removePlayer(e.getPlayer());
    }

    @Deprecated
    public static void callLastOnQuit(PlayerQuitEvent e) {
    }

    private void delete(Player p) {
        gameChest.getDatabaseManager().getAsync().getPlayer(p.getUniqueId(), dbPlayer -> {
            if(dbPlayer.existsPlayer()) {
                gameChest.getNick().unnickOnDisconnect(p);
                gameChest.getDatabaseManager().getAsync().getOnlinePlayer(p.getUniqueId(), DatabaseOnlinePlayer::removeOnlinePlayer);
            }
        });
    }
}
