package de.gamechest.fakeplayer;

import de.gamechest.GameChest;
import org.bukkit.Bukkit;

/**
 * Created by ByteList on 05.01.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class FakePlayerTask implements Runnable {

    private final FakePlayerManager fakePlayerManager = GameChest.getInstance().getFakePlayerManager();

    @Override
    public void run() {
        if (fakePlayerManager.getFakePlayerCount() == 0) return;

        Bukkit.getOnlinePlayers().forEach(player -> fakePlayerManager.getFakePlayers(player.getUniqueId()).forEach(fakePlayer -> {
            fakePlayer.getRunnable().run(player);
        }));
    }
}
