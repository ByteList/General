package de.gamechest.party;

import de.gamechest.GameChest;
import lombok.Getter;

import java.util.ArrayList;

/**
 * Created by ByteList on 09.07.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class Party {

    @Getter
    private final String partyId;


    public Party(String partyId) {
        this.partyId = partyId;
    }

    public String getLeader() {
        return GameChest.getInstance().getDatabaseManager().getDatabaseParty().getLeader(partyId);
    }

    public ArrayList<String> getMember() {
        return GameChest.getInstance().getDatabaseManager().getDatabaseParty().getMembers(partyId);
    }
}
