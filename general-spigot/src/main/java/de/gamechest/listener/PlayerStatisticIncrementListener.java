package de.gamechest.listener;

import com.google.common.base.CaseFormat;
import com.mongodb.MongoClient;
import de.gamechest.GameChest;
import de.gamechest.common.AsyncTasks;
import de.gamechest.database.stats.network.DatabaseNetworkStatsObject;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
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

            BsonDocument document = gameChest.getDatabaseManager().getDatabaseNetworkStats().
                    getDatabaseElement(player.getUniqueId(), DatabaseNetworkStatsObject.MINECRAFT).getAsDocument()
                    .toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry());

            int value = 0;
            if(document.containsKey(finalStatistic)) {
                value = document.getInt64(finalStatistic).intValue();
            }

            document.append(finalStatistic, new BsonInt64(value+add));

            gameChest.getDatabaseManager().getDatabaseNetworkStats().setDatabaseObject(player.getUniqueId(), DatabaseNetworkStatsObject.MINECRAFT, document);
        });
    }
}
