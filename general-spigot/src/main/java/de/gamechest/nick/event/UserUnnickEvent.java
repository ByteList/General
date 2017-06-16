package de.gamechest.nick.event;

import de.gamechest.GameChest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UserUnnickEvent extends Event {
	
	public static HandlerList handerlist = new HandlerList();
	
	private Player p;
	
	public UserUnnickEvent(Player p) {
		this.p = p; 
		
		p.sendMessage(GameChest.getInstance().getNick().prefix + "§bDein Nickname wurde zurückgesetzt.");
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
