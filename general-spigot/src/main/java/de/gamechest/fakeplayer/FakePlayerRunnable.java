package de.gamechest.fakeplayer;

import org.bukkit.entity.Player;

/**
 * Created by ByteList on 06.07.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public interface FakePlayerRunnable {

    void run(FakePlayer fakePlayer, Player player);
}
