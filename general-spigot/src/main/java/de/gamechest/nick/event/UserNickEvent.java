package de.gamechest.nick.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UserNickEvent extends Event {
	
	private static HandlerList handerlist = new HandlerList();
	
	private String playername;
	private String nickname;
	private Player p;
	
	public UserNickEvent(Player p, String playername, String nickname) {
		this.p = p;
		this.playername = playername;
		this.nickname = nickname;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public String getPlayername() {
		return playername;
	}
	
	public String getNickname() {
		return nickname;
	}

	public HandlerList getHandlers() {
	    return handerlist;
	}
	
	public static HandlerList getHandlerList() {
	    return handerlist;
	}

}
