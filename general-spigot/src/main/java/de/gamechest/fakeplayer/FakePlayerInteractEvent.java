package de.gamechest.fakeplayer;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by ByteList on 05.01.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FakePlayerInteractEvent extends Event {

    @Getter
    private static HandlerList handlerList = new HandlerList();

    @Getter
    private FakePlayer fakePlayer;
    @Getter
    private Player player;
    @Getter
    private Action action;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }


    public enum Action {
        INTERACT,
        INTERACT_AT,
        ATTACK
    }
}
