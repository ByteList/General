package de.gamechest.listener;

import de.gamechest.GameChest;
import de.gamechest.database.rank.Rank;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 * Created by ByteList on 29.12.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ChatListener implements Listener {

    private final GameChest gameChest = GameChest.getInstance();
    private final String[] plot = { "/p","/plot","/ps","/plotsquared","/p2","/2","/plotme", "//pos2", "//pos1", "/party" };

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(ChatEvent e) {
        if(e.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) e.getSender();
            String message = e.getMessage();
            if(message.startsWith("/")) {
                String command = message.split(" ")[0];

                if(command.equalsIgnoreCase("/pl") || command.equalsIgnoreCase("/plugins")) {
                    if(!gameChest.hasRank(player.getUniqueId(), Rank.DEVELOPER)) {
//                        e.setMessage("/fakeplugins s6adD4g146 exec");
                        e.setCancelled(true);
                        player.sendMessage("§fPlugins (x): §aAAC§f, §aPlotMe§f, §aWorldGuard§f, §cAuthMe§f, §aEssentials§f, §aPermissionEx§f, §eCloud");
                        return;
                    }
                }

                if(player.getServer().getInfo().getName().equalsIgnoreCase("BauEvent")) {
                    for (String plotCmd : plot) {
                        if (command.equalsIgnoreCase(plotCmd)) {
                            if (!gameChest.hasRank(player.getUniqueId(), Rank.DEVELOPER)) {
                                e.setCancelled(true);
                                gameChest.sendNoPermissionMessage(player);
                                return;
                            }
                        }
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
