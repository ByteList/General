package de.gamechest.party;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by ByteList on 08.07.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PartyManager {

    private HashMap<String, Party> parties = new HashMap<>();
    private HashMap<UUID, String> partyIds = new HashMap<>();

    public Party createParty(ProxiedPlayer leader) {
        Party party = new Party(leader);
        parties.put(party.getPartyId(), party);
        partyIds.put(leader.getUniqueId(), party.getPartyId());
        return party;
    }

    public boolean existsParty(String partyId) {
        return parties.containsKey(partyId);
    }

    public boolean isPlayerInAParty(UUID uuid) {
        return partyIds.containsKey(uuid);
    }

    public Party getParty(UUID uuid) {
        return parties.getOrDefault(partyIds.getOrDefault(uuid, null), null);
    }

    public void deleteParty(Party party) {
        for(ProxiedPlayer player : party.getMember()) {
            if(this.partyIds.containsKey(player.getUniqueId()))
                this.partyIds.remove(player.getUniqueId());
        }
        if(this.partyIds.containsKey(party.getLeader().getUniqueId()))
            this.partyIds.remove(party.getLeader().getUniqueId());
        parties.remove(party.getPartyId());
        party.deleteParty();
    }

    public void acceptRequest(String partyId, ProxiedPlayer player) {
        Party party = parties.get(partyId);
        if(party != null) {
            partyIds.put(player.getUniqueId(), partyId);
            party.acceptRequest(player);
        }
    }

    public void removeMember(String partyId, ProxiedPlayer player) {
        Party party = parties.get(partyId);
        if(party != null) {
            party.removeMember(player);
            partyIds.remove(player.getUniqueId());
        }
    }

}
