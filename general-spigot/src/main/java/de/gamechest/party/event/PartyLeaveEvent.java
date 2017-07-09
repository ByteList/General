package de.gamechest.party.event;

import de.gamechest.party.Party;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by ByteList on 09.07.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PartyLeaveEvent extends Event {

    @Getter
    private static HandlerList handlerList = new HandlerList();

    @Getter
    private Player player;
    @Getter
    private Party party;

    public PartyLeaveEvent(String partyId, Player player) {
        this.player = player;
        this.party = new Party(partyId);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
