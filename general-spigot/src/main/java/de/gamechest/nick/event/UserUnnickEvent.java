package de.gamechest.nick.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UserUnnickEvent extends Event {
	
	private static HandlerList handerlist = new HandlerList();
	
	private Player p;
	
	public UserUnnickEvent(Player p) {
		this.p = p;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	
	public HandlerList getHandlers() {
	    return handerlist;
	}
	
	public static HandlerList getHandlerList() {
	    return handerlist;
	}
}
