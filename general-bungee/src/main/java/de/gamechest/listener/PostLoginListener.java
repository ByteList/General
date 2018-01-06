package de.gamechest.listener;

import de.gamechest.GameChest;
import de.gamechest.database.DatabaseManager;
import de.gamechest.database.rank.Rank;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 * Created by ByteList on 26.04.2017.
 */
public class PostLoginListener implements Listener {

    private GameChest gameChest = GameChest.getInstance();
    private DatabaseManager databaseManager = gameChest.getDatabaseManager();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPostLogin(PostLoginEvent e) {
        ProxiedPlayer pp = e.getPlayer();

        if(gameChest.equalsRank(pp.getUniqueId(), Rank.DEVELOPER)) {
            int waiting = databaseManager.getDatabaseBugreport().getWaitingReports().size();
            if(waiting > 0) {
                pp.sendMessage(gameChest.pr_bug+"§bEs existieren §e"+waiting+"§b offene Bug-Reports!");
            }
        }
        if(gameChest.hasRank(pp.getUniqueId(), Rank.BUILDER)) {
            gameChest.onlineTeam.add(pp);
        }
    }
}
