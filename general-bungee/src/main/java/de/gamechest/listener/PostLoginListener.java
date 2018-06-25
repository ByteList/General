package de.gamechest.listener;

import de.gamechest.GameChest;
import de.gamechest.database.DatabaseManager;
import de.gamechest.database.rank.Rank;
import de.gamechest.database.terms.DatabaseTermsObject;
import net.md_5.bungee.api.ProxyServer;
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
        ProxiedPlayer player = e.getPlayer();

        if(databaseManager.getDatabaseTerms().existsPlayer(player.getUniqueId()) &&
                databaseManager.getDatabaseTerms().getDatabaseElement(player.getUniqueId(), DatabaseTermsObject.STATE).getAsInt() == 0) {

            player.connect(ProxyServer.getInstance().getServerInfo("Pre-Login"));
            return;
        }

        if(gameChest.equalsRank(player.getUniqueId(), Rank.DEVELOPER)) {
            int waiting = databaseManager.getDatabaseBugreport().getWaitingReports().size();
            if(waiting > 0) {
                player.sendMessage(gameChest.pr_bug+"§bEs existieren §e"+waiting+"§b offene Bug-Reports!");
            }
        }
        if(gameChest.hasRank(player.getUniqueId(), Rank.BUILDER)) {
            gameChest.onlineTeam.add(player);
        }
    }
}
