package de.gamechest.listener;

import com.google.common.base.CaseFormat;
import de.gamechest.common.AsyncTasks;
import de.gamechest.GameChest;
import de.gamechest.database.stats.network.DatabaseNetworkStatsObject;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

/**
 * Created by ByteList on 24.02.2019.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PlayerStatisticIncrementListener implements Listener {

    private final GameChest gameChest = GameChest.getInstance();


    @EventHandler
    public void onStatisticIncrement(PlayerStatisticIncrementEvent e) {
        Player player = e.getPlayer();
        String statistic = "stat."+CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, e.getStatistic().name());
        int add = e.getNewValue()-e.getPreviousValue();

        if(e.getStatistic().isSubstatistic()) {
            String subStatistic = "";
            /* like this:
            "stat.useItem.minecraft.stonebrick": 11,
             */

            switch (e.getStatistic().getType()) {
                case UNTYPED:
                    break;
                case ITEM:
                    subStatistic = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, e.getMaterial().name());
                    break;
                case BLOCK:
                    subStatistic = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, e.getMaterial().name());
                    break;
                case ENTITY:
                    subStatistic = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, e.getEntityType().name());
                    break;
            }

            statistic = statistic + "." + subStatistic;
        }

        String finalStatistic = statistic;
        AsyncTasks.getInstance().runTaskAsync(()-> {
            gameChest.getDatabaseManager().getDatabaseNetworkStats().createPlayer(player.getUniqueId());

            Document document = gameChest.getDatabaseManager().getDatabaseNetworkStats().
                    getDatabaseElement(player.getUniqueId(), DatabaseNetworkStatsObject.MINECRAFT).getAsDocument();

            int value = 0;
            if(document.containsKey(finalStatistic)) {
                value = document.getInteger(finalStatistic);
            }

            document.append(finalStatistic, value+add);

            gameChest.getDatabaseManager().getDatabaseNetworkStats().setDatabaseObject(player.getUniqueId(), DatabaseNetworkStatsObject.MINECRAFT, document);
        });
    }
}
