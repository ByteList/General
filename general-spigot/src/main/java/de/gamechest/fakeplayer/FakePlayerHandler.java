package de.gamechest.fakeplayer;

import de.gamechest.FakePlayer;

import java.util.HashMap;

/**
 * Created by ByteList on 05.01.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class FakePlayerHandler {

    private HashMap<Integer, FakePlayer> fakePlayers = new HashMap<>();

    public boolean addFakePlayer(FakePlayer fakePlayer) {
        if(!existsFakePlayer(fakePlayer.getEntityId())) {
            fakePlayers.put(fakePlayer.getEntityId(), fakePlayer);
            return true;
        }
        return false;
    }

    public boolean removeFakePlayer(int entityId) {
        if(existsFakePlayer(entityId)) {

            fakePlayers.remove(entityId);
            return true;
        }
        return false;
    }

    public boolean existsFakePlayer(int entityId) {
        return false;
    }

    public FakePlayer[] getFakePlayers() {
        return fakePlayers.values().toArray(new FakePlayer[fakePlayers.size()]);
    }
}
