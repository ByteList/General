package de.gamechest.party.event;

import de.gamechest.party.Party;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by ByteList on 09.07.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PartyDeleteEvent extends Event {

    @Getter
    private static HandlerList handlerList = new HandlerList();

    @Getter
    private Party party;

    public PartyDeleteEvent(Party party) {
        this.party = party;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
