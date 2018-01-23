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

    private static final GameChest gameChest = GameChest.getInstance();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        gameChest.rankCache.remove(e.getPlayer().getUniqueId());

        if(Bukkit.getServerName().contains("nonBungee")) {
            delete(e.getPlayer());
        }

        gameChest.getPacketInjector().removePlayer(e.getPlayer());
    }

    @Deprecated
    public static void callLastOnQuit(PlayerQuitEvent e) {
    }

    /**
     * Is used by some plugins but it's a logical mistake from me.
     *
     * The method have to called at the end of the PlayerQuitListener.
     *
     * @param e
     */
    @Deprecated
    public static void OLD_callFirstOnQuit(PlayerQuitEvent e) {
    }

    private void delete(Player p) {
        GameChest.getInstance().getNick().unnickOnDisconnect(p);
        gameChest.getDatabaseManager().getAsync().getOnlinePlayer(p.getUniqueId(), DatabaseOnlinePlayer::removeOnlinePlayer);
    }
}
