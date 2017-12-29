package de.gamechest.listener;

import de.gamechest.GameChest;
import de.gamechest.database.rank.Rank;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by ByteList on 29.12.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ChatListener implements Listener {

    private final GameChest gameChest = GameChest.getInstance();

    @EventHandler
    public void onChat(ChatEvent e) {
        if(e.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) e.getSender();
            String message = e.getMessage();
            if(message.startsWith("/")) {
                String command = message.split(" ")[0];

                if(command.equalsIgnoreCase("/pl") || command.equalsIgnoreCase("/plugins")) {
                    if(!gameChest.hasRank(player.getUniqueId(), Rank.DEVELOPER)) {
                        e.setMessage("/fakeplugins s6adD4g146 exec");
                    }
                }

                if(command.startsWith("/minecraft:")) {
                    e.setMessage(message.replace("/minecraft:", "/"));
                }

                if(command.startsWith("/bukkit:")) {
                    e.setMessage(message.replace("/bukkit:", "/"));
                }

                if(command.startsWith("/spigot:")) {
                    e.setMessage(message.replace("/spigot:", "/"));
                }
            }
        }
    }
}
