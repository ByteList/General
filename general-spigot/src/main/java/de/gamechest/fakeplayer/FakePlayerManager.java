package de.gamechest.fakeplayer;

import de.gamechest.GameChest;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by ByteList on 02.07.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class FakePlayerManager {

    private HashMap<UUID, ArrayList<FakePlayer>> fakePlayers = new HashMap<>();

    public FakePlayerManager() {
        GameChest.getInstance().getPacketInjector().registerListener(new FakePlayerPacketHandleListener());
        Bukkit.getScheduler().scheduleSyncRepeatingTask(GameChest.getInstance(), ()-> {
            Bukkit.getOnlinePlayers().forEach(player -> getFakePlayers(player.getUniqueId()).forEach(fakePlayer -> {
                fakePlayer.getRunnable().run(fakePlayer, player);
            }));
        }, 60L, 2L);
    }

    public ArrayList<FakePlayer> getFakePlayers(UUID uuid) {
        return fakePlayers.getOrDefault(uuid, new ArrayList<>());
    }

    public void addNewFakePlayer(UUID uuid, boolean spawn, FakePlayer... fakePlayer) {
        ArrayList<FakePlayer> fakePlayers = getFakePlayers(uuid);
        fakePlayers.addAll(Arrays.asList(fakePlayer));
        this.fakePlayers.put(uuid, fakePlayers);
        if(spawn) {
            for (FakePlayer fake : fakePlayer) {
                fake.spawn();
            }
        }
    }

    public void removeFakePlayer(UUID uuid, int... entityIds) {
        ArrayList<FakePlayer> fakePlayers = getFakePlayers(uuid), clone = new ArrayList<>(fakePlayers);

        for (int id : entityIds) {
            for (FakePlayer fakePlayer : clone) {
                if(fakePlayer.getEntityId() == id) {
                    fakePlayer.destroy();
                    fakePlayers.remove(fakePlayer);
                }
            }
        }

        this.fakePlayers.put(uuid, fakePlayers);
    }
}
