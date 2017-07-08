package de.gamechest.listener;

import de.gamechest.GameChest;
import de.gamechest.database.DatabaseManager;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by ByteList on 10.04.2017.
 */
public class QuitListener {

    public static void callLastOnQuit(PlayerQuitEvent e) {
        OLD_callFirstOnQuit(e);
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

        if(Bukkit.getServerName().contains("nonBungee")) {
            delete(e.getPlayer());
        }

        GameChest gameChest = GameChest.getInstance();
        gameChest.getPacketInjector().removePlayer(e.getPlayer());
    }

    private static void delete(Player p) {
        DatabaseManager databaseManager= GameChest.getInstance().getDatabaseManager();
        GameChest.getInstance().getNick().unnickOnDisconnect(p);

        databaseManager.getAsync().getOnlinePlayer(p.getUniqueId(), DatabaseOnlinePlayer::removeOnlinePlayer);
    }
}
